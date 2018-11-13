package io.dymatics.cogny.support;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.androidannotations.annotations.EReceiver;

import io.dymatics.cogny.Constants;

@EReceiver
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        CognyService_.start(context);
        setResultCode(Activity.RESULT_OK);
    }
}
