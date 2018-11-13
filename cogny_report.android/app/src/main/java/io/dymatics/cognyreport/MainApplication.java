package io.dymatics.cognyreport;

import android.content.Context;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;
import android.widget.Toast;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EApplication;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.greenrobot.eventbus.EventBus;

import io.dymatics.cognyreport.support.PrefsService_;
import io.dymatics.cognyreport.support.SQLiteOpenHelper;

@EApplication
public class MainApplication extends MultiDexApplication {
    @Pref PrefsService_ prefs;

    @AfterInject
    void afterInject() {
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
        if (prefs.initializedDbStatus().get() == 3) { // failed
            deleteDatabase(getString(R.string.db_name));
            toast(R.string.message_common_db_fail);
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @UiThread
    void toast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_LONG).show();
    }
}
