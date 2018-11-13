package io.dymatics.cognyreport.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.androidannotations.ormlite.annotations.OrmLiteDao;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import io.dymatics.cognyreport.BuildConfig;
import io.dymatics.cognyreport.On;
import io.dymatics.cognyreport.domain.model.Diagnosis;
import io.dymatics.cognyreport.domain.model.User;
import io.dymatics.cognyreport.domain.model.UserMobileDevice;
import io.dymatics.cognyreport.support.PrefsService_;
import io.dymatics.cognyreport.support.Query;
import io.dymatics.cognyreport.support.SQLiteOpenHelper;
import lombok.Getter;

@EBean(scope = EBean.Scope.Singleton)
public class CognyBean {
    @RootContext Context context;

    @OrmLiteDao(helper = SQLiteOpenHelper.class) SQLiteOpenHelper.UserDao userDao;
    @OrmLiteDao(helper = SQLiteOpenHelper.class) SQLiteOpenHelper.UserMobileDeviceDao userMobileDeviceDao;

    @Bean RestClient restClient;

    @Pref PrefsService_ prefs;
    @SystemService TelephonyManager telephonyManager;

    @AfterInject
    void afterInject() {
//        if (!EventBus.getDefault().isRegistered(this)) {
//            EventBus.getDefault().register(this);
//        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////


    @SuppressLint("MissingPermission")
    private String getDeviceUUID() {
        String tmDevice = "" + telephonyManager.getDeviceId();
        String tmSerial = "" + telephonyManager.getSimSerialNumber();
        String androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        return new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode()).toString();
    }

    @SuppressLint("MissingPermission")
    private String getMobileNumber() {
        try{
            return PhoneNumberUtils.formatNumber(telephonyManager.getLine1Number(), Locale.KOREA.getCountry()).replace("+82", "0").replace("-", "");
        }catch(Exception e){
            return null;
        }
    }

    public String currentSignProvider() {
        User user = loadUser();
        return user == null ? null : user.getSignProvider();
    }

    public void pushUserMobile() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> {
            UserMobileDevice.Form form = UserMobileDevice.form(getDeviceUUID(), getMobileNumber(), instanceIdResult.getToken(), true);
            restClient.postUserMobiles(form, new On<UserMobileDevice>().addSuccessListener(result -> saveUserMobileDevice(result)));
        });
    }

    public long getUserNo() {
        User user = loadUser();
        return user == null || user.getUserNo() == null ? 0L : user.getUserNo();
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

    public UserMobileDevice loadUserMobileDevice() {
        return executeSync(() -> userMobileDeviceDao.queryBuilder().queryForFirst());
    }

    public void saveUser(User user, On<User> on) {
        execute(() -> {
            userDao.deleteBuilder().delete();
            userDao.create(user);
            return null;
        }, on);
    }

    @Getter private List<Diagnosis.Category> diagnosisCategories;
    public void loadDiagnosisCategories() {
        restClient.fetchDiagnosisCategories(new On<Diagnosis.Categories>().addSuccessListener(categories -> diagnosisCategories = categories));
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
