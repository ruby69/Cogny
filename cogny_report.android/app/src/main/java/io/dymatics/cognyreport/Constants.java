package io.dymatics.cognyreport;

import android.Manifest;
import android.util.Log;

import org.apache.commons.lang3.time.FastDateFormat;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public interface Constants {
    FastDateFormat FORMAT_DATETIME = FastDateFormat.getInstance("yyyy-MM-dd HH:mm", TimeZone.getDefault(), Locale.getDefault());
    FastDateFormat FORMAT_DATETIME_CSV = FastDateFormat.getInstance("yyyyMMddHHmm", TimeZone.getDefault(), Locale.getDefault());
    FastDateFormat FORMAT_DATETIME_CSV_LOG = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss.SSS", TimeZone.getDefault(), Locale.getDefault());
    FastDateFormat FORMAT_LOG = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss.SSS]#", TimeZone.getDefault(), Locale.getDefault());

    FastDateFormat FORMAT_DATE_YMD = FastDateFormat.getInstance("yyyy-MM-dd", TimeZone.getDefault(), Locale.getDefault());
    FastDateFormat FORMAT_DATE_YMD_OBD = FastDateFormat.getInstance("yy-MM-dd HH:mm:ss", TimeZone.getDefault(), Locale.getDefault());
    FastDateFormat FORMAT_DATE_YMD_KO = FastDateFormat.getInstance("yyyy년 MM월 dd일", TimeZone.getDefault(), Locale.getDefault());
    FastDateFormat FORMAT_DATE_YMD_KO2 = FastDateFormat.getInstance("yyyy년 M월 d일", TimeZone.getDefault(), Locale.getDefault());
    FastDateFormat FORMAT_ELAPSED_DHM = FastDateFormat.getInstance("dd일 HH시간 mm분", TimeZone.getDefault(), Locale.getDefault());
    FastDateFormat FORMAT_ELAPSED_HM = FastDateFormat.getInstance("HH시간 mm분", TimeZone.getDefault(), Locale.getDefault());
    FastDateFormat FORMAT_TIME_HMS = FastDateFormat.getInstance("HH:mm:ss", TimeZone.getDefault(), Locale.getDefault());
    FastDateFormat FORMAT_TIME_HM = FastDateFormat.getInstance("H:mm", TimeZone.getDefault(), Locale.getDefault());
    FastDateFormat FORMAT_DATETIME_HISTORY = FastDateFormat.getInstance("MM/dd HH:mm", TimeZone.getDefault(), Locale.getDefault());
    FastDateFormat FORMAT_DATETIME_DTC_ISSUED = FastDateFormat.getInstance("M/d H:mm", TimeZone.getDefault(), Locale.getDefault());
    FastDateFormat FORMAT_DATETIME_DTC_DELETED = FastDateFormat.getInstance("MM/dd HH:mm 소멸", TimeZone.getDefault(), Locale.getDefault());

    NumberFormat FORMAT_CURRENCY = NumberFormat.getInstance(Locale.KOREA);
    DecimalFormat FORMAT_DISTANCE_1 = new DecimalFormat("주행거리 #,###km");
    DecimalFormat FORMAT_DISTANCE_2 = new DecimalFormat("#,###.#km");
    DecimalFormat FORMAT_DISTANCE_3 = new DecimalFormat("#,###km");
    DecimalFormat FORMAT_FUEL_MILEAGE = new DecimalFormat("#,###.#km/L");
    DecimalFormat FORMAT_FUEL_CONSUMPTION = new DecimalFormat("#,###.#L");
    DecimalFormat FORMAT_COUNT_ITEM = new DecimalFormat("#,###건");
    DecimalFormat FORMAT_COUNT_ITEM2 = new DecimalFormat("#,###대");

    long MILLIS_10 = 10L;
    long MILLIS_20 = 20L;
    long MILLIS_50 = 50L;
    long MILLIS_100 = 100L;
    long MILLIS_150 = 150L;
    long MILLIS_200 = 200L;
    long SEC_1 = 1000L;
    long SEC_5 = 5 * SEC_1;
    long SEC_10 = 10 * SEC_1;
    long SEC_30 = 30L * SEC_1;
    long MIN_1 = 60L * SEC_1;
    long MIN_30 = 30L * MIN_1;
    long HOUR_1 = 60L * MIN_1;
    long HOUR_10 = 10L * HOUR_1;
    long DAY_1 = 24L * HOUR_1;

    String FORMAT_STRING_TIME_S_KO = "%02d초";
    String FORMAT_STRING_TIME_MS_KO = "%02d분 %02d초";
    String FORMAT_STRING_TIME_HMS_KO = "%02d시간 %02d분 %02d초";
    String FORMAT_STRING_TIME_DHMS_KO = "%d일 %02d시간 %02d분 %02d초";

    String EXTRA_DEVICE_ADDRESS = "device_address";
    String EXTRA_DEVICE_NAME = "device_name";

    int REQUEST_ENABLE_BT = 1;
    int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    int REQUEST_ENABLE_LOCATION = 3;
    String[] REQUEST_PERMISSIONS = {Manifest.permission.READ_PHONE_STATE};
    String[] FILE_PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    int ID_REQUEST_PERMISSIONS = 999;
    int ID_REQUEST_SIGNIN_WITH_GOOGLE = 901;
    int ID_REQUEST_REVOKE_WITH_GOOGLE = 902;

    String SIGN_PROVIDER_GOOGLE = "GOOGLE";
    String SIGN_PROVIDER_COGNY = "COGNY";
    static boolean isSignedWithGoogle(String provider) {
        return SIGN_PROVIDER_GOOGLE.equals(provider);
    }

    static void log(String message) {
        if (BuildConfig.DEBUG) {
            Log.d("cognyrepair", message);
        }
    }

    static String convertDate(String dateStr) {
        if (dateStr != null) {
            try {
                dateStr = FORMAT_DATE_YMD_KO2.format(FORMAT_DATE_YMD.parse(dateStr));
            } catch (ParseException ex){
                return dateStr;
            }
        }
        return dateStr;
    }
}
