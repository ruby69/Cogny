package io.dymatics.cogny.support;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EReceiver;
import org.androidannotations.annotations.ReceiverAction;

import io.dymatics.cogny.domain.model.ActivityLog;
import io.dymatics.cogny.service.ActivityLogHandler;

@EReceiver
public class CommonReceiver extends BroadcastReceiver {
    @Bean ActivityLogHandler activityLogHandler;

    @ReceiverAction(actions = Intent.ACTION_BOOT_COMPLETED)
    void onActionBootComplete(Context context) {
        activityLogHandler.log(ActivityLog.Category.APP, ActivityLog.Code.APP10004, versionCode(context));
    }

    @ReceiverAction(actions = Intent.ACTION_MY_PACKAGE_REPLACED)
    void onActionMyPackageReplaced(Context context) {
        activityLogHandler.log(ActivityLog.Category.APP, ActivityLog.Code.APP10003, versionCode(context));
    }

    @ReceiverAction(actions = Intent.ACTION_PACKAGE_REPLACED, dataSchemes = "package")
    void onActionPackageReplaced(Context context, Intent intent) {
        if (intent.getDataString().contains("io.dymatics.cogny")){
            activityLogHandler.log(ActivityLog.Category.APP, ActivityLog.Code.APP10003, versionCode(context));
        }
    }

    @ReceiverAction(actions = {Intent.ACTION_SHUTDOWN})
    void onActionPowerOff(Context context) {
        activityLogHandler.log(ActivityLog.Category.APP, ActivityLog.Code.APP10005, versionCode(context));
        CognyService_.intent(context).stop();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
    }

    private int versionCode(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch(Exception e) {
            return 0;
        }
    }
}