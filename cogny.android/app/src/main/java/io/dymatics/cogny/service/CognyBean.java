package io.dymatics.cogny.service;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.androidannotations.ormlite.annotations.OrmLiteDao;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import io.dymatics.cogny.BuildConfig;
import io.dymatics.cogny.Constants;
import io.dymatics.cogny.On;
import io.dymatics.cogny.R;
import io.dymatics.cogny.domain.model.ActivityLog;
import io.dymatics.cogny.domain.model.DriveHistory;
import io.dymatics.cogny.domain.model.DtcRaw;
import io.dymatics.cogny.domain.model.Fota;
import io.dymatics.cogny.domain.model.ObdDevice;
import io.dymatics.cogny.domain.model.RepairMsg;
import io.dymatics.cogny.domain.model.SensingLog;
import io.dymatics.cogny.domain.model.Sensings;
import io.dymatics.cogny.domain.model.User;
import io.dymatics.cogny.domain.model.UserMobileDevice;
import io.dymatics.cogny.domain.model.Vehicle;
import io.dymatics.cogny.event.OnDriveHistoryUpdated;
import io.dymatics.cogny.event.OnVehicleDetect;
import io.dymatics.cogny.event.OnVehicleUnknown;
import io.dymatics.cogny.event.obd.ObdAction;
import io.dymatics.cogny.event.obd.ObdDetected;
import io.dymatics.cogny.event.obd.ObdFotaAction;
import io.dymatics.cogny.event.obd.ObdFotaFin;
import io.dymatics.cogny.event.obd.ObdInfo;
import io.dymatics.cogny.event.obd.ObdInvalid;
import io.dymatics.cogny.event.obd.ObdJobSensing;
import io.dymatics.cogny.event.obd.ObdMode;
import io.dymatics.cogny.event.obd.ObdPosting;
import io.dymatics.cogny.support.PrefsService_;
import io.dymatics.cogny.support.Query;
import io.dymatics.cogny.support.SQLiteOpenHelper;
import io.dymatics.cogny.support.obd.ObdBean;
import io.dymatics.cogny.support.obd.Protocol;
import io.dymatics.cogny.support.obd.message.ObdInfoMessage;
import lombok.Getter;
import lombok.Setter;

@EBean(scope = EBean.Scope.Singleton)
public class CognyBean {
    @RootContext Context context;

    @OrmLiteDao(helper = SQLiteOpenHelper.class) SQLiteOpenHelper.UserDao userDao;
    @OrmLiteDao(helper = SQLiteOpenHelper.class) SQLiteOpenHelper.UserMobileDeviceDao userMobileDeviceDao;
    @OrmLiteDao(helper = SQLiteOpenHelper.class) SQLiteOpenHelper.ObdDeviceDao obdDeviceDao;
    @OrmLiteDao(helper = SQLiteOpenHelper.class) SQLiteOpenHelper.VehicleDao vehicleDao;
    @OrmLiteDao(helper = SQLiteOpenHelper.class) SQLiteOpenHelper.DriveHistoryDao driveHistoryDao;
    @OrmLiteDao(helper = SQLiteOpenHelper.class) SQLiteOpenHelper.SensingLogDao sensingLogDao;
    @OrmLiteDao(helper = SQLiteOpenHelper.class) SQLiteOpenHelper.DtcRawDao dtcRawDao;
    @OrmLiteDao(helper = SQLiteOpenHelper.class) SQLiteOpenHelper.FotaDao fotaDao;

    @Bean RestClient restClient;
    @Bean ObdBean obdBean;

    @Pref PrefsService_ prefs;
    @SystemService TelephonyManager telephonyManager;
    @SystemService BluetoothManager bluetoothManager;

    private BluetoothAdapter bluetoothAdapter;

    @Getter private boolean scanBle;
    @Setter private boolean openedMain;

    @AfterInject
    void afterInject() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        bluetoothAdapter = bluetoothManager.getAdapter();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean isScannableBle() {
        return bluetoothAdapter != null;
    }

    @Background
    public void scanBle(ScanCallback scanCallback, On<Void> on) {
        on.ready();
        if (isScannableBle()) {
            BluetoothLeScanner bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
            if (bluetoothLeScanner != null) {
                delayedStopScan(scanCallback, on);
                Constants.activityLog(ActivityLog.Category.BT, ActivityLog.Code.BT10001);
                bluetoothLeScanner.startScan(scanCallback);
                scanBle = true;
            }
        } else {
            on.complete(null);
        }
    }

    @Background
    public void stopScan(ScanCallback scanCallback, On<Void> on) {
        BluetoothLeScanner bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        if (bluetoothLeScanner != null) {
            bluetoothLeScanner.stopScan(scanCallback);
            Constants.activityLog(ActivityLog.Category.BT, ActivityLog.Code.BT10002);
            scanBle = false;
        }
        on.complete(null);
    }

    @Background(delay = Constants.SCAN_PERIOD)
    void delayedStopScan(ScanCallback scanCallback, On<Void> on) {
        stopScan(scanCallback, on);
    }

    public void detectObd() {
        long gap = System.currentTimeMillis() - prefs.lastDisconnectedTime().get();
        if (gap > Constants.MIN_4 && !isObdConnected() && !isScanBle()) {
            Constants.activityLog(ActivityLog.Category.JOB, ActivityLog.Code.JOB10001);
            scanBle(new ScanCallback() {
                        @Override
                        public void onScanResult(int callbackType, ScanResult result) {
                            BluetoothDevice device = result.getDevice();
                            String deviceName = device.getName();
                            if (device.getAddress().equals(prefs.deviceAddress().getOr(null))
                                    || (device.getType() == BluetoothDevice.DEVICE_TYPE_LE && result.getRssi() > -70 && deviceName != null && deviceName.length() > 0 && (deviceName.startsWith("COGNY") || deviceName.startsWith("SECO") || deviceName.startsWith("chipsen")))) {
                                EventBus.getDefault().post(new ObdDetected(result.getDevice()));
                            }
                        }
                    }, new On<>()
            );
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Vehicle loadVehicle() {
        return executeSync(() -> vehicleDao.queryBuilder().queryForFirst());
    }

    public void saveVehicle(Vehicle vehicle, On<Void> on) {
        execute(() -> {
            vehicleDao.deleteBuilder().delete();
            vehicleDao.create(vehicle);
            return null;
        }, on);
    }

    public ObdDevice loadObdDevice() {
        return executeSync(() -> obdDeviceDao.queryBuilder().queryForFirst());
    }

    private void saveObdDevice(ObdDevice obdDevice, On<Void> on) {
        execute(() -> {
            obdDeviceDao.deleteBuilder().delete();
            obdDeviceDao.create(obdDevice);
            return null;
        }, on);
    }

    public UserMobileDevice loadUserMobileDevice() {
        return executeSync(() -> userMobileDeviceDao.queryBuilder().queryForFirst());
    }

    private void saveUserMobileDevice(UserMobileDevice userMobileDevice) {
        execute(() -> {
            userMobileDeviceDao.deleteBuilder().delete();
            userMobileDeviceDao.create(userMobileDevice);
            return null;
        });
    }

    public User loadUser() {
        return executeSync(() -> userDao.queryBuilder().queryForFirst());
    }

    public void saveUser(User user, On<User> on) {
        execute(() -> {
            userDao.deleteBuilder().delete();
            userDao.create(user);
            return null;
        }, on);
    }

    public void loadLatestDriveHistory(On<DriveHistory> on) {
        execute(() -> driveHistoryDao.queryBuilder().orderBy(DriveHistory.FIELD_pk, false).queryForFirst(), on);
    }

    public void loadLatestRepairMsg(On<RepairMsg> on) {
        long vehicleNo = getVehicleNo();
        if (vehicleNo > 0L) {
            restClient.fetchRepairMsg(vehicleNo, on);
        }
    }

    public void clean() {
        execute(() -> {
            dtcRawDao.deleteBuilder().delete();
            sensingLogDao.deleteBuilder().delete();
            driveHistoryDao.deleteBuilder().delete();
            vehicleDao.deleteBuilder().delete();
            obdDeviceDao.deleteBuilder().delete();
            userMobileDeviceDao.deleteBuilder().delete();
            userDao.deleteBuilder().delete();
            fotaDao.deleteBuilder().delete();

            return null;
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Getter @Setter private DriveHistory driveHistory;
    @Getter private boolean driving;

    @SuppressLint("MissingPermission")
    private String getDeviceUUID() {
        String tmDevice = "" + telephonyManager.getDeviceId();
        String tmSerial = "" + telephonyManager.getSimSerialNumber();
        String androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        return new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode()).toString();
    }

    @SuppressLint("MissingPermission")
    private String getMobileNumber() {
        if (BuildConfig.DEBUG) {
            return "01026106969";
        } else {
            try{
                return PhoneNumberUtils.formatNumber(telephonyManager.getLine1Number(), Locale.KOREA.getCountry()).replace("+82", "0").replace("-", "");
            }catch(Exception e){
                return null;
            }
        }
    }

    public String currentSignProvider() {
        User user = loadUser();
        return user == null ? null : user.getSignProvider();
    }

    public long getUserNo() {
        User user = loadUser();
        return user == null || user.getUserNo() == null ? 0L : user.getUserNo();
    }

    private long getUserMobileDeviceNo() {
        UserMobileDevice userMobileDevice = loadUserMobileDevice();
        return userMobileDevice == null || userMobileDevice.getUserMobileDeviceNo() == null ? 0L : userMobileDevice.getUserMobileDeviceNo();
    }

    public long getMobileDeviceNo() {
        UserMobileDevice userMobileDevice = loadUserMobileDevice();
        return userMobileDevice == null || userMobileDevice.getMobileDeviceNo() == null ? 0L : userMobileDevice.getMobileDeviceNo();
    }

    public long getObdDeviceNo() {
        ObdDevice obdDevice = loadObdDevice();
        return obdDevice == null || obdDevice.getObdDeviceNo() == null ? 0L : obdDevice.getObdDeviceNo();
    }

    public long getVehicleNo() {
        Vehicle vehicle = loadVehicle();
        return vehicle == null || vehicle.getVehicleNo() == null ? 0L : vehicle.getVehicleNo();
    }

    public long getDriveHistoryNo() {
        return driveHistory == null || driveHistory.getDriveHistoryNo() == null ? 0L : driveHistory.getDriveHistoryNo();
    }

    public String getObdSerial() {
        ObdDevice obdDevice = loadObdDevice();
        return obdDevice == null ? null : obdDevice.getObdSerial();
    }

    public void pushUserMobile() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> {
            UserMobileDevice.Form form = UserMobileDevice.form(getDeviceUUID(), getMobileNumber(), instanceIdResult.getToken(), true);
            restClient.postUserMobiles(form, new On<UserMobileDevice>().addSuccessListener(result -> saveUserMobileDevice(result)));
        });
    }

    public void pushUserMobile(String token) {
        if (prefs.allGrantedPermissions().get()) {
            restClient.postUserMobiles(UserMobileDevice.form(getDeviceUUID(), getMobileNumber(), token, true), new On<UserMobileDevice>().addSuccessListener(result -> {}));
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean isObdConnected() {
        return obdBean.isConnected();
    }

    public void sensing() {
        if (obdBean.isLinkedCommunication()) {
            obdBean.writeRequest();
        }
    }

    @Subscribe
    public void onEvent(ObdInfo event) {
        ObdInfoMessage obdInfoMessage = event.getObdInfoMessage();
        String serial = obdInfoMessage.getSerial();

        Constants.activityLog(ActivityLog.Category.OBD, ActivityLog.Code.OBD10001, serial, obdInfoMessage.getFwVersion(), obdInfoMessage.getTableVersion());
        Constants.log(String.format("Saved obd old-serial is [%s]", getObdSerial()));
        Constants.log(String.format("Detected obd serial is [%s]", serial));
        if (serial.equals(getObdSerial())) {
            applyObd(obdInfoMessage);
        } else {
            // clean previous records..
            EventBus.getDefault().post(new ObdJobSensing(false));
            execute(() -> {
                driving = false;
                setDriveHistory(null);

                dtcRawDao.deleteBuilder().delete();
                sensingLogDao.deleteBuilder().delete();
                driveHistoryDao.deleteBuilder().delete();
                vehicleDao.deleteBuilder().delete();
                return null;
            }, new On<>().addCompleteListener(r -> fetchObdDevice(obdInfoMessage)));

        }
    }

    private void fetchObdDevice(ObdInfoMessage obdInfoMessage) {
        Constants.activityLog(ActivityLog.Category.OBD, ActivityLog.Code.OBD10002, obdInfoMessage.getSerial());
        restClient.fetchObdDevice(obdInfoMessage.getSerial(), new On<ObdDevice>().addSuccessListener(result -> {
            if (result != null) {
                saveObdDevice(result, new On<Void>().addSuccessListener(aVoid -> {
                    Constants.activityLog(ActivityLog.Category.OBD, ActivityLog.Code.OBD10005, result.getObdDeviceNo(), result.getObdSerial());
                    applyObd(obdInfoMessage);
                }));
            } else {
                Constants.activityLog(ActivityLog.Category.OBD, ActivityLog.Code.OBD10003, obdInfoMessage.getSerial(), obdInfoMessage.getFwVersion());
                prefs.deviceAddress().remove();
                obdBean.disconnect();
                if (openedMain) {
                    EventBus.getDefault().post(new ObdInvalid());
                } else {
                    toast(R.string.message_obd_invalid);
                }
            }
        }).addFailureListener(t -> Constants.activityLog(ActivityLog.Category.OBD, ActivityLog.Code.OBD10004, t.getMessage())));
    }

    private void applyObd(ObdInfoMessage obdInfoMessage) {
        prefs.firmwareVersion().put(obdInfoMessage.getFwVersion());
        prefs.tableVersion().put(obdInfoMessage.getTableVersion());
        pullFota();
        obdBean.linkCommunication();

        Constants.activityLog(ActivityLog.Category.ETC, ActivityLog.Code.ETC10101, obdInfoMessage.getSerial());
        Vehicle vehicle = loadVehicle();
        if (vehicle == null) {
            restClient.fetchVehicle(getObdDeviceNo(), new On<Vehicle>()
                    .addSuccessListener(result -> {
                        if (result == null) {
                            failedDetectingVehicle();
                        } else {
                            saveVehicle(result, new On<Void>().addSuccessListener(result1 -> succeedDetectingVehicle(result)));
                        }
                    })
                    .addFailureListener(t -> failedDetectingVehicle())
            );
        } else {
            succeedDetectingVehicle(vehicle);
        }
    }

    private void failedDetectingVehicle() {
        Constants.activityLog(ActivityLog.Category.ETC, ActivityLog.Code.ETC10102);
        obdBean.disconnect();
        if (openedMain) {
            EventBus.getDefault().post(new OnVehicleUnknown());
        } else {
            toast(R.string.message_vehicle_empty);
        }
    }

    private void succeedDetectingVehicle(Vehicle vehicle) {
        Constants.activityLog(ActivityLog.Category.ETC, ActivityLog.Code.ETC10103, vehicle.getVehicleNo(), vehicle.getLicenseNo());
        EventBus.getDefault().post(new OnVehicleDetect());
        EventBus.getDefault().post(new ObdJobSensing(true));
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void readyDrive(Date date, int odometer, On<Void> on) {
        if (isReadyDevice() && !driving) {
            DriveHistory driveHistory = new DriveHistory();
            driveHistory.setVehicleNo(getVehicleNo());
            driveHistory.setObdDeviceNo(getObdDeviceNo());
            driveHistory.setUserMobileDeviceNo(getUserMobileDeviceNo());

            driveHistory.setStartDate(date);
            driveHistory.setStartTime(Constants.FORMAT_TIME_HMS.format(date));
            driveHistory.setStartMileage(odometer);
            restClient.postDrives(driveHistory,
                    new On<DriveHistory>()
                            .addSuccessListener(result -> {
                                driving = true;
                                driveHistoryDao.createIfNotExists(result);
                                setDriveHistory(result);
                                on.success(null);
                                Constants.activityLog(ActivityLog.Category.ETC, ActivityLog.Code.ETC10201, result.getDriveHistoryNo());
                            })
                            .addFailureListener(t -> {
                                driving = false;
                                setDriveHistory(null);
                                on.failure(t);
                                Constants.activityLog(ActivityLog.Category.ETC, ActivityLog.Code.ETC10202, t.getMessage());
                            })
                            .addCompleteListener(result -> on.complete(null))
            );
        } else {
            on.complete(null);
        }
    }

    public void finDrive(Date date, On<Void> on) {
        if (driveHistory != null && driving) {
            Constants.activityLog(ActivityLog.Category.ETC, ActivityLog.Code.ETC10205);
            if (date != null) {
                driveHistory.setEndTime(date);
            }
            driveHistory.setDriveDistance(driveHistory.getEndMileage() - driveHistory.getStartMileage());
            driveHistoryDao.update(driveHistory);
            restClient.postDrivef(driveHistory,
                    new On<Void>()
                            .addSuccessListener(result -> {
                                Constants.activityLog(ActivityLog.Category.ETC, ActivityLog.Code.ETC10203, driveHistory.getDriveHistoryNo());
                                driving = false;
                                EventBus.getDefault().post(new OnDriveHistoryUpdated(driveHistory, driving));
                                setDriveHistory(null);
                                on.success(null);
                            })
                            .addFailureListener(t -> {
                                if (driveHistory != null) {
                                    Constants.activityLog(ActivityLog.Category.ETC, ActivityLog.Code.ETC10204, driveHistory.getDriveHistoryNo() + ", " + t.getMessage());
                                } else {
                                    Constants.activityLog(ActivityLog.Category.ETC, ActivityLog.Code.ETC10204, t.getMessage());
                                }
                                on.failure(t);
                            })
                            .addCompleteListener(result -> on.complete(null))
            );
        } else {
            on.complete(null);
        }
    }

    public void forceFinDrive() {
        if (!isObdConnected() && driveHistory != null) {
            Constants.activityLog(ActivityLog.Category.ETC, ActivityLog.Code.ETC10206);
            finDrive(null, new On<>());
        }
    }

    public void updateEndMileage(Date date, int odometer) {
        if (driveHistory != null) {
            driveHistory.setEndMileage(odometer);
            driveHistory.setEndTime(date);
            driveHistory.setDriveDistance(driveHistory.getEndMileage() - driveHistory.getStartMileage());

            EventBus.getDefault().post(new OnDriveHistoryUpdated(driveHistory, driving));
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private boolean isReadyDevice() {
        return getVehicleNo() != 0L && getObdDeviceNo() != 0L && getUserMobileDeviceNo() != 0L;
    }

    private boolean isReadyRecord() {
        return isReadyDevice() && driveHistory != null;
    }

    private SensingLog newSensingLog(String values) {
        SensingLog sensingLog = new SensingLog();
        sensingLog.setVehicleNo(getVehicleNo());
        sensingLog.setObdDeviceNo(getObdDeviceNo());
        sensingLog.setDriveHistoryNo(getDriveHistoryNo());
        sensingLog.setValues(values);
        return sensingLog;
    }

    @Background
    public void recordSensingLog(String values) {
        if (isReadyRecord()) {
            insertSensingLog(newSensingLog(values));
        }
    }

    private void insertSensingLog(SensingLog sensingLog) {
        execute(() -> {
            sensingLogDao.create(sensingLog);
            return null;
        }, new On<Void>());
    }

    private List<SensingLog> selectSensingLogs() {
        try {
            QueryBuilder<SensingLog, Long> qb = sensingLogDao.queryBuilder();
            qb.where().eq(SensingLog.FIELD_uploaded, false);
            return qb.query();
        } catch(Exception e) {
            if(BuildConfig.DEBUG) {
                Log.e(getClass().getName(), e.getMessage(), e);
            }
            return null;
        }
    }

    private List<DtcRaw> selectDtcRaws() {
        try {
            QueryBuilder<DtcRaw, Long> qb = dtcRawDao.queryBuilder();
            qb.where().eq(DtcRaw.FIELD_uploaded, false);
            return qb.query();
        } catch(Exception e) {
            if(BuildConfig.DEBUG) {
                Log.e(getClass().getName(), e.getMessage(), e);
            }
            return null;
        }
    }

    public void postSensings(On<Void> on) {
        List<SensingLog> sensingLogs = selectSensingLogs();
        if (sensingLogs != null && sensingLogs.size() > 0) {
            Sensings sensings = new Sensings();
            sensings.setSensingLogs(sensingLogs);
            sensings.setDtcRaws(selectDtcRaws());
            restClient.postSensing(sensings, new On<Void>()
                    .addSuccessListener(result -> {
                        afterPostSensing(sensings);
                        on.success(null);
                    })
                    .addFailureListener(t -> on.failure(t)));
        }
    }

    private void afterPostSensing(Sensings sensings) {
        cleanSensingLogs(sensings.getSensingLogs());
        resetDtcRaws(sensings.getDtcRaws());
    }

    @Background
    void cleanSensingLogs(List<SensingLog> sensingLogs) {
        for (final SensingLog sensingLog : sensingLogs) {
            sensingLog.setUploaded(true);
            sensingLogDao.update(sensingLog);
        }

        try {
            DeleteBuilder<SensingLog, Long> db = sensingLogDao.deleteBuilder();
            db.where().eq(SensingLog.FIELD_uploaded, true);
            db.delete();
        } catch(Exception e) {
            if(BuildConfig.DEBUG) {
                Log.e(getClass().getName(), e.getMessage(), e);
            }
        }
    }

    @Background
    void resetDtcRaws(List<DtcRaw> dtcRaws) {
        for (final DtcRaw dtcRaw : dtcRaws) {
            dtcRaw.setUploaded(true);
            dtcRawDao.update(dtcRaw);
        }
    }

    private DtcRaw newDtcRaw(String str) {
        DtcRaw dtcRaw = new DtcRaw();
        dtcRaw.setVehicleNo(getVehicleNo());
        dtcRaw.setObdDeviceNo(getObdDeviceNo());
        dtcRaw.setDriveHistoryNo(getDriveHistoryNo());
        dtcRaw.populate(str);
        return dtcRaw;
    }

    private List<DtcRaw> toDtcRaws(List<String> values) {
        List<DtcRaw> list = new ArrayList<>();
        for (String str : values) {
            list.add(newDtcRaw(str));
        }
        return list;
    }

    @Background
    public void recordDtcRaws(List<String> values) {
        if (isReadyRecord()) {
            for (DtcRaw dtcRaw : toDtcRaws(values)) {
                insertUpdateDtcLog(dtcRaw);
            }
        }
    }

    private void insertUpdateDtcLog(DtcRaw dtcRaw) {
        execute(() -> {
            QueryBuilder<DtcRaw, Long> qb = dtcRawDao.queryBuilder().orderBy(DtcRaw.FIELD_pk, false);
            Where<DtcRaw, Long> where = qb.where();
            Where<DtcRaw, Long> cond1 = where.eq(DtcRaw.FIELD_driveHistoryNo, dtcRaw.getDriveHistoryNo());
            Where<DtcRaw, Long> cond2 = where.eq(DtcRaw.FIELD_code, dtcRaw.getCode());
            where.and(cond1, cond2);
            DtcRaw result = qb.queryForFirst();
            if (result != null && result.getState().equals(dtcRaw.getState())) {
                result.setDtcUpdatedTime(dtcRaw.getDtcUpdatedTime());
                result.setUploaded(false);
                dtcRawDao.update(result);
            } else {
                dtcRawDao.create(dtcRaw);
            }

            return null;
        }, new On<Void>());
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////

    private int detectingFotaCount;
    public void pullFota() {
        if (detectingFotaCount++ %2 == 0) {
            Constants.log("Pull firmware fota info");
            restClient.fetchFota(Fota.Type.F, new On<Fota>().addSuccessListener(result -> saveAndDownloadFota(result, prefs.firmwareVersion().get())));
        } else {
            Constants.log("Pull table fota info");
            restClient.fetchFota(Fota.Type.T, new On<Fota>().addSuccessListener(result -> saveAndDownloadFota(result, prefs.tableVersion().get())));
        }
    }

    private void saveFota(Fota fota, On<Void> on) {
        execute(() -> {
            fotaDao.createIfNotExists(fota);
            return null;
        }, on);
    }

    private void updateFota(Fota fota, On<Void> on) {
        execute(() -> {
            fotaDao.update(fota);
            return null;
        }, on);
    }

    private Fota getExistFota(Fota fota) {
        return fotaDao.queryForId(fota.getFotaNo());
    }

    private void saveAndDownloadFota(Fota fota, String localVersion) {
        if (fota != null && localVersion != null && !fota.getVersion().equals(localVersion)){
            Fota exist = getExistFota(fota);
            if (exist != null) {
                String filePath = exist.getFilePath();
                if (filePath == null || filePath.equals("null") || filePath.isEmpty()) {
                    authAndDownload(fota);
                }
            } else {
                saveFota(fota, new On<Void>().addSuccessListener(aVoid -> authAndDownload(fota)));
            }
        }
    }

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private void download(Fota fota) {
        Constants.log("Start downloading fota binary");
        StorageReference httpsReference = storage.getReferenceFromUrl(fota.getUrl());
        try {
            File file = new File(context.getFilesDir(), String.format("%s_%s_%s", fota.getType().name().toLowerCase(), fota.getVersion(), fota.getFotaNo()));
            httpsReference.getFile(file).addOnSuccessListener(taskSnapshot -> {
                fota.setFilePath(file.getAbsolutePath());
                fotaDao.update(fota);

                existFirmwareFota = null;
                existTableFota = null;
                Constants.log("Succeed downloading fota binary");
                Constants.activityLog(ActivityLog.Category.OBD, ActivityLog.Code.OBD10101, fota.getFotaNo(), fota.getType(), fota.getVersion());

            }).addOnCompleteListener(task -> {
                FirebaseUser user = auth.getCurrentUser();
                if (user != null && user.isAnonymous()) {
                    user.delete();
                }
            });
        } catch (Exception e) {
            Constants.log(e.getMessage());
        }
    }

    private void authAndDownload(Fota fota) {
        if (auth.getCurrentUser() != null) {
            download(fota);
        } else {
            auth.signInAnonymously().addOnSuccessListener(authResult -> download(fota));
        }
    }

    private Fota getLatestFota(Fota.Type type) {
        try {
            QueryBuilder<Fota, Long> qb = fotaDao.queryBuilder().orderBy(Fota.FIELD_pk, false);
            qb.where().eq(Fota.FIELD_type, type);
            return qb.queryForFirst();
        } catch(Exception e) {
            if(BuildConfig.DEBUG) {
                Log.e(getClass().getName(), e.getMessage(), e);
            }
        }

        return null;
    }

    private Fota existFirmwareFota;
    private Fota existTableFota;
    private Fota currentFota;
    public void checkFota() {
        if (existFirmwareFota == null) {
            existFirmwareFota = getLatestFota(Fota.Type.F);
        }
        if (existTableFota == null) {
            existTableFota = getLatestFota(Fota.Type.T);
        }

        if ((existFirmwareFota != null && existFirmwareFota.isReady(prefs.firmwareVersion().get())) || (existTableFota != null && existTableFota.isReady(prefs.tableVersion().get()))) {
            Constants.log("Stopping sensing jobs...");
            EventBus.getDefault().post(new ObdJobSensing(false));
            EventBus.getDefault().post(new ObdPosting(false));

            if (existFirmwareFota != null && existFirmwareFota.isReady(prefs.firmwareVersion().get())) {
                doFota(existFirmwareFota);
            } else if (existTableFota != null && existTableFota.isReady(prefs.tableVersion().get())) {
                doFota(existTableFota);
            }
        }
    }

    int bufferSize = 512;
    @Background(delay = Constants.SEC_10)
    void doFota(Fota fota) {
        currentFota = fota;
        Constants.log("Do fota and change mode to handle packet");
        Constants.activityLog(ActivityLog.Category.OBD, ActivityLog.Code.OBD10102, fota.getFotaNo(), fota.getType(), fota.getVersion());
        EventBus.getDefault().post(new ObdMode(ObdMode.Mode.FOTA));

        Protocol.Cmd.Write fotaCmd = fota.getType().isFirmware() ? Protocol.Cmd.Write.NEW_FW_FOTA : Protocol.Cmd.Write.NEW_TABLE_FOTA;
        File file = new File(fota.getFilePath());
        if (file.exists()) {
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
                List<byte[]> list = new ArrayList<>();
                while (bis.available() > 0) {
                    byte[] bytes = new byte[bufferSize];
                    int length = bis.read(bytes);
                    if (length != bufferSize) {
                        byte[] temp = new byte[length];
                        System.arraycopy(bytes, 0, temp, 0, length);
                        bytes = temp;
                    }
                    list.add(bytes);
                }

                ObdFotaAction obdFotaAction = new ObdFotaAction();
                int tCount = list.size();
                for (int cCount = 1; cCount<=tCount; cCount++) {
                    obdFotaAction.add(new ObdAction(fotaCmd, list.remove(0), cCount, tCount));
                }

                EventBus.getDefault().post(obdFotaAction);
            } catch (Exception e) {
            }
        }
    }

    @Subscribe
    public void onEvent(ObdFotaFin event) {
        Constants.activityLog(ActivityLog.Category.OBD, ActivityLog.Code.OBD10103, currentFota.getFotaNo(), currentFota.getType(), currentFota.getVersion());
        Constants.log("wait a minute while finishing fota.. ...");
        EventBus.getDefault().post(new ObdMode(ObdMode.Mode.DATA));
        EventBus.getDefault().post(new ObdPosting(true));

        currentFota.setApplied(true);
        updateFota(currentFota, new On<Void>().addCompleteListener(result -> currentFota = null));
        processFotaFin();
    }

    @Background(delay = Constants.SEC_30)
    void processFotaFin() {
        detectObd();
    }


















    ////////////////////////////////////////////////////////////////////////////////////////////////


    @Background
    <T> void execute(Query<T> query, On<T> on) {
        on.ready();
        T result = null;
        try {
            result = query.execute();
            on.success(result);
        } catch(Exception e) {
            if(BuildConfig.DEBUG) {
                Log.e(getClass().getName(), e.getMessage(), e);
            }
            on.failure(e);
        } finally {
            on.complete(result);
        }
    }

    @Background
    <T> void execute(Query<T> query) {
        try {
            query.execute();
        } catch(Exception e) {
            if(BuildConfig.DEBUG) {
                Log.e(getClass().getName(), e.getMessage(), e);
            }
        }
    }

    private <T> T executeSync(Query<T> query) {
        try {
            return query.execute();
        } catch(Exception e) {
            if(BuildConfig.DEBUG) {
                Log.e(getClass().getName(), e.getMessage(), e);
            }
            return null;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @UiThread
    void toast(int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_LONG).show();
    }
}
