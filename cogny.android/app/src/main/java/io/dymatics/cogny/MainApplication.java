package io.dymatics.cogny;

import android.content.Context;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.FirebaseApp;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EApplication;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.greenrobot.eventbus.EventBus;

import io.dymatics.cogny.domain.model.ActivityLog;
import io.dymatics.cogny.support.CognyService_;
import io.dymatics.cogny.support.PrefsService_;
import io.dymatics.cogny.support.SQLiteOpenHelper;
import io.fabric.sdk.android.Fabric;

@EApplication
public class MainApplication extends MultiDexApplication {
    @Pref PrefsService_ prefs;

    @AfterInject
    void afterInject() {
        if(!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }
        FirebaseApp.initializeApp(this);
        EventBus.builder().logNoSubscriberMessages(false).sendNoSubscriberEvent(false).installDefaultEventBus();
        initializeDatabase();
    }

    @Background
    void initializeDatabase() {
        SQLiteOpenHelper sqliteOpenHelper = new SQLiteOpenHelper(this);
        try {
            sqliteOpenHelper.setPrefs(prefs).getWritableDatabase(); // invoke db initialize method after calling onCreate or onUpgrade.
        } catch (Exception e) {

        } finally {
            sqliteOpenHelper.close();
            postInitializeDatabase();
        }
    }

    @UiThread
    void postInitializeDatabase() {
        if (prefs.initializedDbStatus().get() == 2) { // succeed
            Constants.activityLog(ActivityLog.Category.APP, ActivityLog.Code.APP10001, versionCode(), "create");
            CognyService_.start(this);

        } else if (prefs.initializedDbStatus().get() == 3) { // failed
            deleteDatabase(getString(R.string.db_name));
            toast(R.string.message_common_db_fail);
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onTerminate() {
        Constants.activityLog(ActivityLog.Category.APP, ActivityLog.Code.APP10001, versionCode(), "terminate");
        super.onTerminate();
    }

    @UiThread
    void toast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_LONG).show();
    }

    private long versionCode() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch(Exception e) {
            return 0;
        }
    }
}
