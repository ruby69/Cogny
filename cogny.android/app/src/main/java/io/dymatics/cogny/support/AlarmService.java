package io.dymatics.cogny.support;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.SystemService;
import org.greenrobot.eventbus.EventBus;

import io.dymatics.cogny.Constants;
import io.dymatics.cogny.On;
import io.dymatics.cogny.R;
import io.dymatics.cogny.event.OnNeedUpdate;
import io.dymatics.cogny.service.MiscService;

@EBean(scope = EBean.Scope.Singleton)
public class AlarmService {
    private static final long INTERVAL_DEFAULT = Constants.MIN_15;
    private static final long INTERVAL_VERSION_CHECK = Constants.HOUR_1;
    private static final int NOTIFICATION_ID = 999999901;

    @RootContext Context context;
    @Bean MiscService miscService;
    @SystemService AlarmManager alarmManager;
    @SystemService NotificationManager notificationManager;

    public void reserveWithVersionCheck() {
        miscService.checkVersion(new On<Void>()
                .addSuccessListener(aVoid -> {
                    reserve(INTERVAL_DEFAULT);
                })
                .addFailureListener(aVoid -> {
                    CognyService_.intent(context).stop();
                    EventBus.getDefault().post(new OnNeedUpdate());
                    // reserve(INTERVAL_VERSION_CHECK);
                    notify(context);
                })
        );
    }

    public void reserve(long interval) {
        Intent intent = new Intent(context, AlarmReceiver_.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        long time = System.currentTimeMillis() + interval;
        reserve(pendingIntent, time);
    }

    private void reserve(PendingIntent pendingIntent, long time) {
        alarmManager.cancel(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent);

        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        }
    }

    private void notify(Context context) {
        Intent openIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(context.getString(R.string.url_store_app), context.getPackageName())));
        PendingIntent openPendingIntent = PendingIntent.getActivity(context, 0, openIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = new NotificationCompat.Builder(context, getNotificationChannel(context))
                    .setContentTitle(context.getString(R.string.label_noti_update))
                    .setContentText(context.getString(R.string.label_noti_update_run))
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentIntent(openPendingIntent)
                    .setSound(null)
                    .setAutoCancel(true)
                    .build();
        } else {
            notification = new NotificationCompat.Builder(context)
                    .setContentTitle(context.getString(R.string.label_noti_update))
                    .setContentText(context.getString(R.string.label_noti_update_run))
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentIntent(openPendingIntent)
                    .setAutoCancel(true)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .build();
        }
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    @TargetApi(Build.VERSION_CODES.O)
    private String getNotificationChannel(Context context) {
        String channelId = context.getString(R.string.label_noti_update_channel_id);
        NotificationChannel notificationChannel = notificationManager.getNotificationChannel(channelId);
        if (notificationChannel == null) {
            notificationChannel = new NotificationChannel(channelId, context.getText(R.string.label_noti_update_channel_name), NotificationManager.IMPORTANCE_MIN);
            notificationChannel.setDescription(context.getString(R.string.label_noti_update_channel_name));
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationChannel.setSound(null, null);
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(false);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        return channelId;
    }
}
