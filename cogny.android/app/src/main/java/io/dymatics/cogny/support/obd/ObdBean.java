package io.dymatics.cogny.support.obd;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.SupposeBackground;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.androidannotations.api.BackgroundExecutor;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import io.dymatics.cogny.BuildConfig;
import io.dymatics.cogny.Constants;
import io.dymatics.cogny.R;
import io.dymatics.cogny.domain.model.ActivityLog;
import io.dymatics.cogny.event.obd.ObdAction;
import io.dymatics.cogny.event.obd.ObdConnStatus;
import io.dymatics.cogny.event.obd.ObdConnected;
import io.dymatics.cogny.event.obd.ObdDetected;
import io.dymatics.cogny.event.obd.ObdDisconnect;
import io.dymatics.cogny.event.obd.ObdDisconnected;
import io.dymatics.cogny.event.obd.ObdFotaAction;
import io.dymatics.cogny.event.obd.ObdJobSensing;
import io.dymatics.cogny.event.obd.ObdMode;
import io.dymatics.cogny.support.PrefsService_;
import lombok.Getter;
import lombok.Setter;

@EBean(scope = EBean.Scope.Singleton)
public class ObdBean {
    private static final UUID OBD_SERVICE = UUID.fromString("6E400001-B5A3-...");
    private static final UUID OBD_NOTIFY = UUID.fromString("6E400003-B5A3-...");
    private static final UUID OBD_NOTIFY_CONFIG = UUID.fromString("00002902-0000-...");
    private static final UUID OBD_WRITE = UUID.fromString("6E400002-B5A3-...");

    @RootContext Context context;
    @Pref PrefsService_ prefs;
    @Bean PacketDataHandler packetDataHandler;
    @Bean PacketFotaHandler packetFotaHandler;
    @SystemService BluetoothManager bluetoothManager;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGatt bluetoothGatt;
    private BluetoothDevice device;
    private BluetoothGattCharacteristic notifyCharacteristic = null;
    private BluetoothGattCharacteristic writeCharacteristic = null;
    @Getter private boolean connected;
    @Getter @Setter private boolean linkedCommunication;
    private Handler handler = new Handler();

    private boolean fota;

    @AfterInject
    void afterInject() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null) {
            toast(R.string.message_bt_not_supported);
        }

        device = null;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void connect() {
        Constants.activityLog(ActivityLog.Category.BT, ActivityLog.Code.BLE10002);
        Constants.log("Connect bluetooth gatt");
        characteristicChanged = false;
        if (connected && device != null) {
            Constants.activityLog(ActivityLog.Category.BT, ActivityLog.Code.BLE10003);
            EventBus.getDefault().post(new ObdConnected(device.getName(), device.getAddress()));
        } else {
            packetDataHandler.initialize();
            packetFotaHandler.initialize();

            String address = prefs.deviceAddress().getOr(null);
            if (address != null) {
                Constants.activityLog(ActivityLog.Category.BT, ActivityLog.Code.BLE10004, address);
                Constants.log(String.format("Try connecting to [%s]", address));
                BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
                device.connectGatt(context, false, newBluetoothGattCallback());
            }
        }
    }

    public void disconnect() {
        Constants.activityLog(ActivityLog.Category.BT, ActivityLog.Code.BLE10006);
        EventBus.getDefault().post(new ObdJobSensing(false));
        writeCmd(Protocol.Cmd.Write.DISCONNECT);
        if (bluetoothGatt != null) {
            disableNotify();
            handler.postDelayed(() -> {
                if (bluetoothGatt != null) {
                    bluetoothGatt.disconnect();
                }
            }, 1000);
        } else {
            notifyDisconnect();
        }
    }

    private void notifyDisconnect() {
        Constants.activityLog(ActivityLog.Category.BT, ActivityLog.Code.BLE10008);
        connected = false;
        linkedCommunication = false;
        EventBus.getDefault().post(new ObdJobSensing(false));
        EventBus.getDefault().post(new ObdDisconnected());
        prefs.lastDisconnectedTime().put(System.currentTimeMillis());
    }

    private void close() {
        Constants.activityLog(ActivityLog.Category.BT, ActivityLog.Code.BLE10009);
        Constants.log("Close bluetooth gatt");
        if (bluetoothGatt != null) {
            handler.postDelayed(() -> {
                if (bluetoothGatt != null) {
                    bluetoothGatt.close();
                    bluetoothGatt = null;
                }
                writeCharacteristic = null;
                notifyCharacteristic = null;
            }, 500);
        }
    }

    private void disableNotify() {
        if (notifyCharacteristic != null) {
            BluetoothGattDescriptor descriptor = notifyCharacteristic.getDescriptor(OBD_NOTIFY_CONFIG);
            if (descriptor != null) {
                bluetoothGatt.setCharacteristicNotification(notifyCharacteristic, false);
                descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
                bluetoothGatt.writeDescriptor(descriptor);
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private BluetoothGattCallback newBluetoothGattCallback() {
        return new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                Constants.activityLog(ActivityLog.Category.BT, ActivityLog.Code.BLE10001, MAP_STATUS.get(status), MAP_STATE.get(newState));
                Constants.log(String.format("onConnectionStateChange - status=%s, newState=%s", MAP_STATUS.get(status), MAP_STATE.get(newState)));

                bluetoothGatt = gatt;
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    device = gatt.getDevice();
                    Constants.activityLog(ActivityLog.Category.BT, ActivityLog.Code.BLE10005, device.getAddress());
                    connected = true;

                    BackgroundExecutor.cancelAll("connectCheck", true);
                    gatt.discoverServices();

                    prefs.deviceAddress().put(device.getAddress());
                    EventBus.getDefault().post(new ObdMode(ObdMode.Mode.DATA));
                    EventBus.getDefault().post(new ObdConnected(device.getName(), device.getAddress()));
                    Constants.log(String.format("Connected to %s[%s]", device.getName(), device.getAddress()));

                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    if (device != null) {
                        Constants.activityLog(ActivityLog.Category.BT, ActivityLog.Code.BLE10007, device.getAddress());
                        Constants.log(String.format("Disconnected from %s[%s]", device.getName(), device.getAddress()));
                    } else {
                        Constants.activityLog(ActivityLog.Category.BT, ActivityLog.Code.BLE10007, "Device is null");
                        Constants.log("Disconnected");
                    }
                    device = null;
                    notifyDisconnect();
                    close();
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                Constants.log(String.format("onServicesDiscovered - status=%s", MAP_STATUS.get(status)));
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    BluetoothGattService bluetoothGattService = gatt.getService(OBD_SERVICE);
                    if (bluetoothGattService != null) {
                        writeCharacteristic = bluetoothGattService.getCharacteristic(OBD_WRITE);
                        writeCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);

                        notifyCharacteristic = bluetoothGattService.getCharacteristic(OBD_NOTIFY);
                        BluetoothGattDescriptor descriptor = notifyCharacteristic.getDescriptor(OBD_NOTIFY_CONFIG);
                        if (descriptor != null) {
                            gatt.setCharacteristicNotification(notifyCharacteristic, true);
                            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            gatt.writeDescriptor(descriptor);
                        }
                    }
                }

                connectCheck();
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicChanged(gatt, characteristic);
                if (notifyCharacteristic == characteristic) {
                    characteristicChanged = true;
                    byte[] value = characteristic.getValue();
                    if (fota) {
                        packetFotaHandler.handle(value);
                    } else {
                        packetDataHandler.handle(value);
                    }
                }
            }
        };
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////

    boolean characteristicChanged;
    int reConnectCount;
    @Background(delay = Constants.SEC_3, id = "connectCheck", serial = "connectCheck")
    void connectCheck() {
        Constants.activityLog(ActivityLog.Category.BT, ActivityLog.Code.BLE10010);
        if (!characteristicChanged) {
            reConnect();
        } else {
            reConnectCount = 0;
        }
    }

    void reConnect() {
        reConnectCount++;
        if (reConnectCount < 5) {
            Constants.activityLog(ActivityLog.Category.BT, ActivityLog.Code.BLE10011, reConnectCount);
            disconnect();
            handler.postDelayed(() -> {
                connect();
            }, 3000);
        } else {
            reConnectCount = 0;
        }
    }



    public void writeRequest() {
        writeCmd(Protocol.Cmd.Write.REQ);
    }

    @Subscribe
    public void onEvent(ObdConnStatus event) {
        if (device != null) {
            if (connected) {
                EventBus.getDefault().post(new ObdConnected(device.getName(), device.getAddress()));
            } else {
                EventBus.getDefault().post(new ObdDisconnected());
            }
        }
    }

    @Subscribe
    @Background(id = "obdAction", serial = "obdAction")
    public void onEvent(ObdAction event) {
        writeCmd(event.getCmdBytes(), DEFAULT_TRY_COUNT);
    }

    @Background(id = "writeCmd", serial = "writeCmd")
    void writeCmd(Protocol.Cmd.Write cmd) {
        writeCmd(Protocol.Cmd.Write.getBytes(cmd), DEFAULT_TRY_COUNT);
    }

    private static final int DEFAULT_TRY_COUNT = 3;
    private static final int CHUNK_TRY_COUNT = 10;
    private int tryCount = 0;
    @SupposeBackground
    synchronized void writeCmd(byte[] bytes, int supposeTryCount) {
        try {
            tryCount = 0;
            if (bluetoothGatt != null && writeCharacteristic != null) {
                writeCharacteristic.setValue(bytes);
                boolean result = false;
                do {
                    if (bluetoothGatt != null) {
                        result = bluetoothGatt.writeCharacteristic(writeCharacteristic);
                    }
                    log(bytes, "TX(" + result + ") => ");
                    if (!result) {
                        try {Thread.sleep(500L);} catch (Exception e) {}
                    }
                } while (!result && ++tryCount < supposeTryCount);
                try {Thread.sleep(5L);} catch (Exception e) {}
            }
        } catch (Exception e) {
            e.printStackTrace();
            disconnect();
        }
    }

    @Background(id = "writeAsChunk", serial = "writeAsChunk")
    public void writeAsChunk(byte[] bytes) {
        for (byte[] chunk : convertChunks(bytes)) {
            writeCmd(chunk, CHUNK_TRY_COUNT);
        }
    }

    private static final int LENGTH_PER_CHUNK = 20;
    private byte[][] convertChunks(byte[] bytes) {
        int chunkCount = (int) Math.ceil((double) bytes.length / LENGTH_PER_CHUNK);
        byte[][] chunks = new byte[chunkCount][LENGTH_PER_CHUNK];
        for (int i = 0; i < chunkCount; ++i) {
            int start = i * LENGTH_PER_CHUNK;
            int length = Math.min(bytes.length - start, LENGTH_PER_CHUNK);
            byte[] chunk = new byte[length];
            System.arraycopy(bytes, start, chunk, 0, length);
            chunks[i] = chunk;
        }

        return chunks;
    }

    @Subscribe
    public void onEvent(ObdMode event) {
        fota = event.isFota();
    }

    @Subscribe
    public void onEvent(ObdFotaAction event) {
        packetFotaHandler.startFota(event.getActions());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Subscribe
    public void onEvent(ObdDisconnect event) {
        disconnect();
    }

    @Subscribe
    public void onEvent(ObdDetected event) {
        BluetoothDevice device = event.getDevice();
        if (device == null || isConnected()) {
            return;
        }

        prefs.deviceAddress().put(device.getAddress());
        connect();
    }

    public void linkCommunication() {
        linkedCommunication = true;
        writeCmd(Protocol.Cmd.Write.ACK);
        onEvent(new ObdAction(Protocol.Cmd.Write.RTC, Constants.rtcBytes()));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @UiThread
    void toast(int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_LONG).show();
    }

    private void log(byte[] bytes, String prefix) {
        if (!BuildConfig.FLAVOR.equals("forRelease")) {
            StringBuilder sb = new StringBuilder(prefix);
            for (byte b : bytes) {
                sb.append(String.format("%02X ", b));
            }
            Constants.log(sb.toString());
        }
    }

    private static Map<Integer, String> MAP_STATE = new HashMap<>();
    private static Map<Integer, String> MAP_STATUS = new HashMap<>();
    static {
        MAP_STATUS.put(0, "0(0x00) BLE_HCI_STATUS_CODE_SUCCESS");
        MAP_STATUS.put(1, "1(0x01) BLE_HCI_STATUS_CODE_UNKNOWN_BTLE_COMMAND");
        MAP_STATUS.put(2, "2(0x02) BLE_HCI_STATUS_CODE_UNKNOWN_CONNECTION_IDENTIFIER");
        MAP_STATUS.put(5, "5(0x05) BLE_HCI_AUTHENTICATION_FAILURE");
        MAP_STATUS.put(6, "6(0x06) BLE_HCI_STATUS_CODE_PIN_OR_KEY_MISSING");
        MAP_STATUS.put(7, "7(0x07) BLE_HCI_MEMORY_CAPACITY_EXCEEDED");
        MAP_STATUS.put(8, "8(0x08) BLE_HCI_CONNECTION_TIMEOUT");
        MAP_STATUS.put(12, "12(0x0C) BLE_HCI_STATUS_CODE_COMMAND_DISALLOWED");
        MAP_STATUS.put(18, "18(0x12) BLE_HCI_STATUS_CODE_INVALID_BTLE_COMMAND_PARAMETERS");
        MAP_STATUS.put(19, "19(0x13) BLE_HCI_REMOTE_USER_TERMINATED_CONNECTION");
        MAP_STATUS.put(20, "20(0x14) BLE_HCI_REMOTE_DEV_TERMINATION_DUE_TO_LOW_RESOURCES");
        MAP_STATUS.put(21, "21(0x15) BLE_HCI_REMOTE_DEV_TERMINATION_DUE_TO_POWER_OFF");
        MAP_STATUS.put(22, "22(0x16) BLE_HCI_LOCAL_HOST_TERMINATED_CONNECTION");
        MAP_STATUS.put(26, "26(0x1A) BLE_HCI_UNSUPPORTED_REMOTE_FEATURE");
        MAP_STATUS.put(30, "30(0x1E) BLE_HCI_STATUS_CODE_INVALID_LMP_PARAMETERS");
        MAP_STATUS.put(31, "31(0x1F) BLE_HCI_STATUS_CODE_UNSPECIFIED_ERROR");
        MAP_STATUS.put(34, "34(0x22) BLE_HCI_STATUS_CODE_LMP_RESPONSE_TIMEOUT");
        MAP_STATUS.put(36, "36(0x24) BLE_HCI_STATUS_CODE_LMP_PDU_NOT_ALLOWED");
        MAP_STATUS.put(40, "40(0x28) BLE_HCI_INSTANT_PASSED");
        MAP_STATUS.put(41, "41(0x29) BLE_HCI_PAIRING_WITH_UNIT_KEY_UNSUPPORTED");
        MAP_STATUS.put(42, "42(0x2A) BLE_HCI_DIFFERENT_TRANSACTION_COLLISION");
        MAP_STATUS.put(58, "58(0x3A) BLE_HCI_CONTROLLER_BUSY");
        MAP_STATUS.put(59, "59(0x3B) BLE_HCI_CONN_INTERVAL_UNACCEPTABLE");
        MAP_STATUS.put(60, "60(0x3C) BLE_HCI_DIRECTED_ADVERTISER_TIMEOUT");
        MAP_STATUS.put(61, "61(0x3D) BLE_HCI_CONN_TERMINATED_DUE_TO_MIC_FAILURE");
        MAP_STATUS.put(62, "62(0x3E) BLE_HCI_CONN_FAILED_TO_BE_ESTABLISHED");
        MAP_STATUS.put(128, "128(0x80) GATT_NO_RESSOURCES");
        MAP_STATUS.put(129, "129(0x81) GATT_INTERNAL_ERROR");
        MAP_STATUS.put(130, "130(0x82) GATT_WRONG_STATE");
        MAP_STATUS.put(131, "131(0x83) GATT_DB_FULL");
        MAP_STATUS.put(132, "132(0x84) GATT_BUSY");
        MAP_STATUS.put(133, "133(0x85) GATT_ERROR");
        MAP_STATUS.put(135, "135(0x87) GATT_ILLEGAL_PARAMETER");

        MAP_STATE.put(0, "0 STATE_DISCONNECTED");
        MAP_STATE.put(1, "1 STATE_CONNECTING");
        MAP_STATE.put(2, "2 STATE_CONNECTED");
        MAP_STATE.put(3, "3 STATE_DISCONNECTING");
    }
}



//    Status  Hex     Description                                               Explanation
//    0       0x00    BLE_HCI_STATUS_CODE_SUCCESS                               Everything ok.
//    1       0x01    BLE_HCI_STATUS_CODE_UNKNOWN_BTLE_COMMAND
//    2       0x02    BLE_HCI_STATUS_CODE_UNKNOWN_CONNECTION_IDENTIFIER
//    5       0x05    BLE_HCI_AUTHENTICATION_FAILURE
//    6       0x06    BLE_HCI_STATUS_CODE_PIN_OR_KEY_MISSING
//    7       0x07    BLE_HCI_MEMORY_CAPACITY_EXCEEDED
//    8       0x08    BLE_HCI_CONNECTION_TIMEOUT                                Could not establish a connection in specified period. Maybe device is currently connected to something else?
//    12      0x0C    BLE_HCI_STATUS_CODE_COMMAND_DISALLOWED
//    18      0x12    BLE_HCI_STATUS_CODE_INVALID_BTLE_COMMAND_PARAMETERS
//    19      0x13    BLE_HCI_REMOTE_USER_TERMINATED_CONNECTION                 Remote device has forced a disconnect.
//    20      0x14    BLE_HCI_REMOTE_DEV_TERMINATION_DUE_TO_LOW_RESOURCES
//    21      0x15    BLE_HCI_REMOTE_DEV_TERMINATION_DUE_TO_POWER_OFF
//    22      0x16    BLE_HCI_LOCAL_HOST_TERMINATED_CONNECTION
//    26      0x1A    BLE_HCI_UNSUPPORTED_REMOTE_FEATURE
//    30      0x1E    BLE_HCI_STATUS_CODE_INVALID_LMP_PARAMETERS
//    31      0x1F    BLE_HCI_STATUS_CODE_UNSPECIFIED_ERROR
//    34      0x22    BLE_HCI_STATUS_CODE_LMP_RESPONSE_TIMEOUT
//    36      0x24    BLE_HCI_STATUS_CODE_LMP_PDU_NOT_ALLOWED
//    40      0x28    BLE_HCI_INSTANT_PASSED
//    41      0x29    BLE_HCI_PAIRING_WITH_UNIT_KEY_UNSUPPORTED
//    42      0x2A    BLE_HCI_DIFFERENT_TRANSACTION_COLLISION
//    58      0x3A    BLE_HCI_CONTROLLER_BUSY
//    59      0x3B    BLE_HCI_CONN_INTERVAL_UNACCEPTABLE
//    60      0x3C    BLE_HCI_DIRECTED_ADVERTISER_TIMEOUT
//    61      0x3D    BLE_HCI_CONN_TERMINATED_DUE_TO_MIC_FAILURE
//    62      0x3E    BLE_HCI_CONN_FAILED_TO_BE_ESTABLISHED
//    128     0x80    GATT_NO_RESSOURCES
//    129     0x81    GATT_INTERNAL_ERROR
//    130     0x82    GATT_WRONG_STATE
//    131     0x83    GATT_DB_FULL
//    132     0x84    GATT_BUSY
//    133     0x85    GATT_ERROR                                                Can be anything, from device not in Range to a random error.
//    135     0x87    GATT_ILLEGAL_PARAMETER
//    137     0x89
