package io.dymatics.cogny.support;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.androidannotations.api.BackgroundExecutor;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import io.dymatics.cogny.BuildConfig;
import io.dymatics.cogny.Constants;
import io.dymatics.cogny.MainActivity_;
import io.dymatics.cogny.On;
import io.dymatics.cogny.R;
import io.dymatics.cogny.domain.model.ActivityLog;
import io.dymatics.cogny.event.OnActivity;
import io.dymatics.cogny.event.obd.ObdAction;
import io.dymatics.cogny.event.obd.ObdConnected;
import io.dymatics.cogny.event.obd.ObdDisconnect;
import io.dymatics.cogny.event.obd.ObdDisconnected;
import io.dymatics.cogny.event.obd.ObdJobDetecting;
import io.dymatics.cogny.event.obd.ObdJobSensing;
import io.dymatics.cogny.event.obd.ObdMode;
import io.dymatics.cogny.event.obd.ObdPosting;
import io.dymatics.cogny.service.ActivityLogHandler;
import io.dymatics.cogny.service.CognyBean;
import io.dymatics.cogny.support.obd.ObdBean;
import io.dymatics.cogny.support.obd.Protocol;
import io.fabric.sdk.android.Fabric;

@EService
public class CognyService extends Service {
    private static final int NOTIFICATION_ID = 999999900;

    @Pref PrefsService_ prefs;
    @Bean AlarmService alarmService;
    @Bean ActivityLogHandler activityLogHandler;
    @Bean ObdBean obdBean;
    @Bean CognyBean cognyBean;
    @SystemService PowerManager powerManager;
    @SystemService NotificationManager notificationManager;

    private Context context;
    private PowerManager.WakeLock wakeLock = null;

    @Override
    public void onCreate() {
        Constants.activityLog(ActivityLog.Category.APP, ActivityLog.Code.APP10002, versionCode(), "create");

        this.context = this;
        if (wakeLock == null) {
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getResources().getText(R.string.app_name).toString());
            wakeLock.acquire();
        }
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        if(!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }
    }

    @Override
    public void onDestroy() {
        Constants.activityLog(ActivityLog.Category.APP, ActivityLog.Code.APP10002, versionCode(), "destroy");
        EventBus.getDefault().post(new ObdDisconnect());
        cancelAllJobs();
        EventBus.getDefault().unregister(this);
        if (wakeLock != null) {
            wakeLock.release();
            wakeLock = null;
        }

        Constants.log("Destroy the CognyService.");
        if (prefs.needUpdate().get()) {
            notificationManager.cancel(NOTIFICATION_ID);
        } else {
            alarmService.reserve(Constants.SEC_30);
        }
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent intent) {
        stopSelf();
    }

    @AfterInject
    void afterInject() {
        startDetecting();
        startPosting();
        startActivityLogPosting();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (prefs.needUpdate().get()) {
            alarmService.reserveWithVersionCheck();
            return START_NOT_STICKY;

        } else {
            Constants.activityLog(ActivityLog.Category.APP, ActivityLog.Code.APP10002, versionCode(), "start");
            Constants.log("Start the CognyService.");
            startForeground(NOTIFICATION_ID, getNotification(R.string.label_noti_disconnected));

            alarmService.reserveWithVersionCheck();
            return START_STICKY;
        }
    }

    private Notification getNotification(int titleResId) {
        Intent openIntent = MainActivity_.intent(context).flags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP).get();
        PendingIntent openPendingIntent = PendingIntent.getActivity(this, 0, openIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = new NotificationCompat.Builder(getApplicationContext(), getNotificationChannel())
                    .setContentTitle(getString(titleResId))
                    .setContentText(getString(R.string.label_noti_launch))
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentIntent(openPendingIntent)
                    .setSound(null)
                    .setAutoCancel(false)
                    .build();
        } else {
            notification = new NotificationCompat.Builder(context)
                    .setContentTitle(getString(titleResId))
                    .setContentText(getString(R.string.label_noti_launch))
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentIntent(openPendingIntent)
                    .setAutoCancel(false)
                    .setPriority(Notification.PRIORITY_MIN)
                    .build();
        }
        notificationManager.notify(NOTIFICATION_ID, notification);
        return notification;
    }

    @TargetApi(Build.VERSION_CODES.O)
    private String getNotificationChannel() {
        String channelId = getString(R.string.label_noti_common_channel_id);
        NotificationChannel notificationChannel = notificationManager.getNotificationChannel(channelId);
        if (notificationChannel == null) {
            notificationChannel = new NotificationChannel(channelId, getText(R.string.label_noti_common_channel_name), NotificationManager.IMPORTANCE_MIN);
            notificationChannel.setDescription(getString(R.string.label_noti_common_channel_name));
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
            notificationChannel.setSound(null, null);
            notificationChannel.enableLights(false);
            notificationChannel.enableVibration(false);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        return channelId;
    }

    private int versionCode() {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch(Exception e) {
            return 0;
        }
    }

    @Subscribe
    @UiThread
    public void onEvent(ObdDisconnected event) {
        if (!prefs.needUpdate().get()) {
            getNotification(R.string.label_noti_disconnected);
        }
    }

    @Subscribe
    @UiThread
    public void onEvent(ObdConnected event) {
        getNotification(R.string.label_noti_connected);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Receiver(actions = BluetoothAdapter.ACTION_STATE_CHANGED, registerAt = Receiver.RegisterAt.OnCreateOnDestroy)
    void onActionStateChanged(Intent intent) {
        int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);
        Constants.activityLog(ActivityLog.Category.BT, ActivityLog.Code.BT10003, state);
        if (state == BluetoothAdapter.STATE_TURNING_OFF) {
            EventBus.getDefault().post(new ObdAction(Protocol.Cmd.Write.DISCONNECT));
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private boolean startedDetecting;
    private int detectingCount = 0;

    @Background(delay = Constants.MIN_1, id = "detecting", serial = "detecting")
    void detecting() {
        try {
            if (startedDetecting) {
                if (++detectingCount % 3 == 0) {
                    cognyBean.forceFinDrive();
                }

                Constants.log("CognyService.detecting()");
                cognyBean.detectObd();
            }
        } catch (Exception e) {

        } finally {
            detecting();
        }
    }

    private void startDetecting() {
        if (!startedDetecting) {
            detectingCount = 0;
            startedDetecting = true;
            detecting();
            Constants.activityLog(ActivityLog.Category.JOB, ActivityLog.Code.JOB10101);
            Constants.log("Start background detecting job");
        }
    }

    private void stopDetecting() {
        if (startedDetecting) {
            startedDetecting = false;
            BackgroundExecutor.cancelAll("detecting", true);
            Constants.activityLog(ActivityLog.Category.JOB, ActivityLog.Code.JOB10102);
            Constants.log("Stop(cancel all) background detecting job");
        }
    }

    @Subscribe
    public void onEvent(ObdJobDetecting event) {
        String deviceAddress = prefs.deviceAddress().getOr(null);
        if (deviceAddress == null) {
            stopDetecting();
        } else {
            if (event.isRun()) {
                if (!cognyBean.isObdConnected()) {
                    startDetecting();
                }
            } else {
                stopDetecting();
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private boolean startedSensing;

    @Background(delay = Constants.SEC_1, id = "sensing", serial = "sensing")
    void sensing() {
        if (startedSensing) {
            cognyBean.sensing();
            sensing();
        }
    }

    private void startSensing() {
        if (!startedSensing) {
            BackgroundExecutor.cancelAll("sensing", true);
            startedSensing = true;
            sensing();
            Constants.activityLog(ActivityLog.Category.JOB, ActivityLog.Code.JOB10201);
            Constants.log("Start background sensing job");
        }
    }

    private void stopSensing() {
        if (startedSensing) {
            startedSensing = false;
            BackgroundExecutor.cancelAll("sensing", true);
            Constants.activityLog(ActivityLog.Category.JOB, ActivityLog.Code.JOB10202);
            Constants.log("Stop(cancel all) background sensing job");
        }
    }

    @Subscribe
    public void onEvent(ObdJobSensing event) {
        if (event.isRun()) {
            startSensing();
        } else {
            stopSensing();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private boolean startedPosting;

    @Background(delay = Constants.SEC_10, id = "posting", serial = "posting")
    void posting() {
        if (startedPosting) {
            cognyBean.postSensings(new On<Void>()
                    .addSuccessListener(result -> {})
                    .addFailureListener(t -> Constants.activityLog(ActivityLog.Category.JOB, ActivityLog.Code.JOB10403, t.getMessage()))
            );
            posting();
        }
    }

    private void startPosting() {
        if (!startedPosting) {
            startedPosting = true;
            posting();
            Constants.activityLog(ActivityLog.Category.JOB, ActivityLog.Code.JOB10401);
            Constants.log("Start background posting job");
        }
    }

    private void stopPosting() {
        if (startedPosting) {
            startedPosting = false;
            BackgroundExecutor.cancelAll("posting", true);
            Constants.activityLog(ActivityLog.Category.JOB, ActivityLog.Code.JOB10402);
            Constants.log("Stop(cancel all) background posting job");
        }
    }

    @Subscribe
    public void onEvent(ObdPosting event) {
        if (event.isRun()) {
            startPosting();
        } else {
            stopPosting();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Subscribe
    public void onEvent(ObdMode event) {
        if (event.isFota()) {
            stopSensing();
        } else if (event.isData()) {
            startSensing();
        }
    }

    private void cancelAllJobs() {
        stopSensing();
        stopPosting();
        stopDetecting();
        stopActivityLogPosting();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private boolean startedLogPosting;

    @Background(delay = Constants.MIN_15, id = "activityLogPosting", serial = "activityLogPosting")
    void activityLogPosting() {
        if (startedLogPosting) {
            activityLogHandler.postActivityLogs(new On<Void>()
                    .addSuccessListener(result -> {})
                    .addFailureListener(t -> Constants.activityLog(ActivityLog.Category.JOB, ActivityLog.Code.JOB10303, t.getMessage())));
            activityLogPosting();
        }
    }

    private void startActivityLogPosting() {
        if (!startedLogPosting) {
            Constants.activityLog(ActivityLog.Category.JOB, ActivityLog.Code.JOB10301);
            startedLogPosting = true;
            activityLogPosting();
        }
    }

    private void stopActivityLogPosting() {
        if (startedLogPosting) {
            Constants.activityLog(ActivityLog.Category.JOB, ActivityLog.Code.JOB10302);
            startedLogPosting = false;
            BackgroundExecutor.cancelAll("activityLogPosting", true);
        }
    }

    @Subscribe
    public void onEvent(OnActivity event) {
        activityLogHandler.log(event.getCategory(), event.getValues());
    }

    //////////////////////////////////////////////////////////////////////////////////////////////// toast

    @UiThread
    void toast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_LONG).show();
    }

    @UiThread
    void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static void start(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(CognyService_.intent(context.getApplicationContext()).get());
        } else {
            CognyService_.intent(context.getApplicationContext()).start();
        }
    }
}
