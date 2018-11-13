package io.dymatics.cogny.service;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.ormlite.annotations.OrmLiteDao;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import io.dymatics.cogny.BuildConfig;
import io.dymatics.cogny.On;
import io.dymatics.cogny.domain.model.ActivityLog;
import io.dymatics.cogny.domain.model.SensingLog;
import io.dymatics.cogny.support.Query;
import io.dymatics.cogny.support.SQLiteOpenHelper;

@EBean(scope = EBean.Scope.Singleton)
public class ActivityLogHandler {
    @RootContext Context context;
    @Bean RestClient restClient;
    @Bean CognyBean cognyBean;
    @OrmLiteDao(helper = SQLiteOpenHelper.class) SQLiteOpenHelper.ActivityLogDao activityLogDao;
    private ObjectMapper objectMapper = new ObjectMapper();

    public void log(ActivityLog.Category category, Object... values) {
        log(category, String.format("%s", StringUtils.join(values, ",")));
    }

    public void log(ActivityLog.Category category, String activityMessage) {
        long userNo = cognyBean.getUserNo();
        if (userNo != 0L) {
            long mobileDeviceNo = cognyBean.getMobileDeviceNo();
            long vehicleNo = cognyBean.getVehicleNo();
            long obdDeviceNo = cognyBean.getObdDeviceNo();
            long driveHistoryNo = cognyBean.getDriveHistoryNo();

            ActivityLog activityLog = new ActivityLog();
            activityLog.setUserNo(userNo);
            activityLog.setMobileDeviceNo(mobileDeviceNo != 0L ? mobileDeviceNo : null);
            activityLog.setVehicleNo(vehicleNo != 0L ? vehicleNo : null);
            activityLog.setObdDeviceNo(obdDeviceNo != 0L ? obdDeviceNo : null);
            activityLog.setDriveHistoryNo(driveHistoryNo != 0L ? driveHistoryNo : null);
            activityLog.setCategory(category);
            activityLog.setActivity(activityMessage);
            activityLog.setActivityTime(System.currentTimeMillis());
            execute(() -> activityLogDao.createIfNotExists(activityLog));
        }
    }

    public void postActivityLogs(On<Void> on) {
        QueryBuilder<ActivityLog, Long> qb = activityLogDao.queryBuilder();
        try {
            qb.where().eq(SensingLog.FIELD_uploaded, false);
            if (qb.countOf() > 0) {
                List<ActivityLog> activityLogs = qb.query();
                List<String> logs = new ArrayList<>();
                for (ActivityLog activityLog : activityLogs) {
                    logs.add(objectMapper.writeValueAsString(activityLog.toArray()));
                }

                restClient.postActivityLogs(logs, new On<Void>()
                        .addSuccessListener(result -> {
                            cleanActivityLogs(activityLogs);
                            on.success(null);
                        })
                        .addFailureListener(t -> {
                            on.failure(t);
                        }));
            }
        } catch(Exception e) {
            if(BuildConfig.DEBUG) {
                Log.e(getClass().getName(), e.getMessage(), e);
            }
        }
    }

    @Background
    void cleanActivityLogs(List<ActivityLog> activityLogs) {
        for (ActivityLog activityLog : activityLogs) {
            activityLog.setUploaded(true);
            activityLogDao.update(activityLog);
        }

        DeleteBuilder<ActivityLog, Long> db = activityLogDao.deleteBuilder();
        try {
            db.where().eq(ActivityLog.FIELD_uploaded, true);
            db.delete();
        } catch(Exception e) {
            if(BuildConfig.DEBUG) {
                Log.e(getClass().getName(), e.getMessage(), e);
            }
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
}
