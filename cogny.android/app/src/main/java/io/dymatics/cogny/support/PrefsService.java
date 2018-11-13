package io.dymatics.cogny.support;

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.DefaultFloat;
import org.androidannotations.annotations.sharedpreferences.DefaultInt;
import org.androidannotations.annotations.sharedpreferences.DefaultLong;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

@SharedPref(value = SharedPref.Scope.UNIQUE)
public interface PrefsService {

    @DefaultBoolean(false)
    boolean allGrantedPermissions();

    @DefaultInt(0)
    int executedCount();

    @DefaultInt(0) // 0 - before, 1 - prgoress, 2 - succeed, 3 - failed
    int initializedDbStatus();

    @DefaultFloat(1.30F)
    float fontScale();

    String deviceAddress();

    String deviceName();


    String firmwareVersion();

    String tableVersion();

    @DefaultLong(0)
    long lastDisconnectedTime();

    @DefaultBoolean(false)
    boolean needUpdate();
}
