package io.dymatics.cogny;

import android.Manifest;
import android.util.Log;

import org.apache.commons.lang3.time.FastDateFormat;
import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import io.dymatics.cogny.domain.model.ActivityLog;
import io.dymatics.cogny.event.OnActivity;

public interface Constants {
    FastDateFormat FORMAT_DATETIME = FastDateFormat.getInstance("yyyy-MM-dd HH:mm", TimeZone.getDefault(), Locale.getDefault());
    FastDateFormat FORMAT_DATETIME_CSV = FastDateFormat.getInstance("yyyyMMddHHmm", TimeZone.getDefault(), Locale.getDefault());
    FastDateFormat FORMAT_DATETIME_CSV_LOG = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss.SSS", TimeZone.getDefault(), Locale.getDefault());
    FastDateFormat FORMAT_LOG = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss.SSS]#", TimeZone.getDefault(), Locale.getDefault());

    FastDateFormat FORMAT_DATE_YMD = FastDateFormat.getInstance("yyyy-MM-dd", TimeZone.getDefault(), Locale.getDefault());
    FastDateFormat FORMAT_DATE_YMD_OBD = FastDateFormat.getInstance("yy-MM-dd HH:mm:ss", TimeZone.getDefault(), Locale.getDefault());
    FastDateFormat FORMAT_DATE_YMD_KO = FastDateFormat.getInstance("yyyy년 M월 d일", TimeZone.getDefault(), Locale.getDefault());
    FastDateFormat FORMAT_ELAPSED_DHM = FastDateFormat.getInstance("dd일 HH시간 mm분", TimeZone.getDefault(), Locale.getDefault());
    FastDateFormat FORMAT_ELAPSED_HM = FastDateFormat.getInstance("HH시간 mm분", TimeZone.getDefault(), Locale.getDefault());
    FastDateFormat FORMAT_TIME_HMS = FastDateFormat.getInstance("HH:mm:ss", TimeZone.getDefault(), Locale.getDefault());
    FastDateFormat FORMAT_TIME_HM = FastDateFormat.getInstance("H:mm", TimeZone.getDefault(), Locale.getDefault());

    NumberFormat FORMAT_CURRENCY = NumberFormat.getInstance(Locale.KOREA);
    DecimalFormat FORMAT_DISTANCE_1 = new DecimalFormat("주행거리 #,###km");
    DecimalFormat FORMAT_DISTANCE_2 = new DecimalFormat("#,###.#km");
    DecimalFormat FORMAT_DISTANCE_3 = new DecimalFormat("#,###km");
    DecimalFormat FORMAT_FUEL_MILEAGE = new DecimalFormat("#,###.#km/L");
    DecimalFormat FORMAT_FUEL_CONSUMPTION = new DecimalFormat("#,###.#L");

    long MILLIS_10 = 10L;
    long MILLIS_20 = 20L;
    long MILLIS_50 = 50L;
    long MILLIS_100 = 100L;
    long MILLIS_150 = 150L;
    long MILLIS_200 = 200L;
    long SEC_1 = 1000L;
    long SEC_3 = 3 * SEC_1;
    long SEC_5 = 5 * SEC_1;
    long SEC_10 = 10 * SEC_1;
    long SEC_30 = 30L * SEC_1;
    long MIN_1 = 60L * SEC_1;
    long MIN_3 = 3L * MIN_1;
    long MIN_4 = 4L * MIN_1;
    long MIN_5 = 5L * MIN_1;
    long MIN_15 = 15L * MIN_1;
    long MIN_30 = 30L * MIN_1;
    long HOUR_1 = 60L * MIN_1;
    long HOUR_10 = 10L * HOUR_1;
    long DAY_1 = 24L * HOUR_1;

    String FORMAT_STRING_TIME_S_KO = "%d초";
    String FORMAT_STRING_TIME_MS_KO = "%d분 %d초";
    String FORMAT_STRING_TIME_HMS_KO = "%d시간 %d분";
    String FORMAT_STRING_TIME_DHMS_KO = "%d일 %d시간 %d분";

    String EXTRA_DEVICE_ADDRESS = "device_address";
    String EXTRA_DEVICE_NAME = "device_name";

    int REQUEST_ENABLE_BT = 1;
    int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    int REQUEST_ENABLE_LOCATION = 3;
    String[] REQUEST_PERMISSIONS = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    String[] FILE_PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    int ID_REQUEST_PERMISSIONS = 999;
    int ID_REQUEST_SIGNIN_WITH_GOOGLE = 901;
    int ID_REQUEST_REVOKE_WITH_GOOGLE = 902;

    String SIGN_PROVIDER_GOOGLE = "GOOGLE";
    String SIGN_PROVIDER_COGNY = "COGNY";
    static boolean isSignedWithGoogle(String provider) {
        return SIGN_PROVIDER_GOOGLE.equals(provider);
    }

    long SCAN_PERIOD = 5000L;

    static byte[] rtcBytes() {
        Calendar calendar = Calendar.getInstance();
        return new byte[]{
                (byte) (calendar.get(Calendar.YEAR) % 100),
                (byte) (calendar.get(Calendar.MONTH) + 1),
                (byte) (calendar.get(Calendar.DAY_OF_MONTH)),
                (byte) (calendar.get(Calendar.HOUR_OF_DAY)),
                (byte) (calendar.get(Calendar.MINUTE)),
                (byte) (calendar.get(Calendar.SECOND))
        };
    }

    static void log(String message) {
        if (BuildConfig.DEBUG) {
            Log.d("cogny_log", message);
        }
    }

    static void activityLog(ActivityLog.Category category, Object...values) {
        EventBus.getDefault().post(new OnActivity(category, values));
    }

    static String diffTime(Date startTime) {
        return diffTime(startTime, Calendar.getInstance().getTime());
    }

    static String diffTime(Date startTime, Date endTime) {
        long diff = endTime.getTime() - startTime.getTime();
        long diffS = (diff / Constants.SEC_1) % 60;
        long diffM = (diff / Constants.MIN_1) % 60;
        long diffH = (diff / Constants.HOUR_1) % 24;
        long diffD = diff / Constants.DAY_1;
        String result = String.format(Locale.getDefault(), Constants.FORMAT_STRING_TIME_S_KO, diffS);

        if (diff > Constants.DAY_1) {
            result = String.format(Locale.getDefault(), Constants.FORMAT_STRING_TIME_DHMS_KO, diffD, diffH, diffM);
        } else if (diff > Constants.HOUR_1) {
            result = String.format(Locale.getDefault(), Constants.FORMAT_STRING_TIME_HMS_KO, diffH, diffM);
        } else if (diff > Constants.MIN_1) {
            result = String.format(Locale.getDefault(), Constants.FORMAT_STRING_TIME_MS_KO, diffM, diffS);
        }
        return result;
    }

    static String time(long time) {
        long timeS = (time) % 60;
        long timeM = (time / 60) % 60;
        long timeH = (time / 3600) % 24;
        long timeD = time / (3600 * 24);
        String result = String.format(Locale.getDefault(), Constants.FORMAT_STRING_TIME_S_KO, timeS);

        if (time > 3600 * 24) {
            result = String.format(Locale.getDefault(), Constants.FORMAT_STRING_TIME_DHMS_KO, timeD, timeH, timeM);
        } else if (time > 3600) {
            result = String.format(Locale.getDefault(), Constants.FORMAT_STRING_TIME_HMS_KO, timeH, timeM);
        } else if (time > 60) {
            result = String.format(Locale.getDefault(), Constants.FORMAT_STRING_TIME_MS_KO, timeM, timeS);
        }
        return result;
    }
}
