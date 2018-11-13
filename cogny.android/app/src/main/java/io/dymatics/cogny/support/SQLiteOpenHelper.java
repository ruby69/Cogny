package io.dymatics.cogny.support;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;

import io.dymatics.cogny.BuildConfig;
import io.dymatics.cogny.R;
import io.dymatics.cogny.domain.model.ActivityLog;
import io.dymatics.cogny.domain.model.Backup;
import io.dymatics.cogny.domain.model.DriveHistory;
import io.dymatics.cogny.domain.model.DtcRaw;
import io.dymatics.cogny.domain.model.Fota;
import io.dymatics.cogny.domain.model.ObdDevice;
import io.dymatics.cogny.domain.model.SensingLog;
import io.dymatics.cogny.domain.model.User;
import io.dymatics.cogny.domain.model.UserMobileDevice;
import io.dymatics.cogny.domain.model.Vehicle;
import io.dymatics.cogny.event.OnCompleteOrmLite;
import io.dymatics.cogny.event.ProgressMessage;

public class SQLiteOpenHelper extends OrmLiteSqliteOpenHelper {
    private Context context;
    private PrefsService_ prefs;

    public SQLiteOpenHelper(Context context) {
        super(context, context.getString(R.string.db_name), null, context.getResources().getInteger(R.integer.db_version));
        this.context = context;
    }

    public SQLiteOpenHelper setPrefs(PrefsService_ prefs) {
        this.prefs = prefs;
        return this;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            prefs.initializedDbStatus().put(1);

            dropAndCreateTables(connectionSource);
            initialize(database);

            prefs.initializedDbStatus().put(2);
        } catch (Exception e) {
            prefs.initializedDbStatus().put(3);
            if (BuildConfig.DEBUG) {
                Log.e(getClass().getName(), e.getMessage(), e);
            }
        } finally {
            EventBus.getDefault().post(new OnCompleteOrmLite());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            prefs.initializedDbStatus().put(1);

            if (oldVersion > 3) {
                createBackupTables(connectionSource);
                backup(database);

                dropAndCreateTables(connectionSource);
                initialize(database);

                restoreDatas(database);
                dropBackupTables(connectionSource);
            } else {
                dropAndCreateTables(connectionSource);
                initialize(database);
            }


            prefs.initializedDbStatus().put(2);
        } catch (Exception e) {
            prefs.initializedDbStatus().put(3);
            if (BuildConfig.DEBUG) {
                Log.e(getClass().getName(), e.getMessage(), e);
            }
        } finally {
            EventBus.getDefault().post(new OnCompleteOrmLite());
        }
    }

    private void dropAndCreateTables(ConnectionSource connectionSource) throws Exception {
        TableUtils.dropTable(connectionSource, Fota.class, true);
        TableUtils.dropTable(connectionSource, DtcRaw.class, true);
        TableUtils.dropTable(connectionSource, SensingLog.class, true);
        TableUtils.dropTable(connectionSource, DriveHistory.class, true);
        TableUtils.dropTable(connectionSource, Vehicle.class, true);
        TableUtils.dropTable(connectionSource, ObdDevice.class, true);
        TableUtils.dropTable(connectionSource, UserMobileDevice.class, true);
        TableUtils.dropTable(connectionSource, User.class, true);
        TableUtils.dropTable(connectionSource, ActivityLog.class, true);

        TableUtils.createTable(connectionSource, ActivityLog.class);
        TableUtils.createTable(connectionSource, User.class);
        TableUtils.createTable(connectionSource, UserMobileDevice.class);
        TableUtils.createTable(connectionSource, ObdDevice.class);
        TableUtils.createTable(connectionSource, Vehicle.class);
        TableUtils.createTable(connectionSource, DriveHistory.class);
        TableUtils.createTable(connectionSource, SensingLog.class);
        TableUtils.createTable(connectionSource, DtcRaw.class);
        TableUtils.createTable(connectionSource, Fota.class);
    }

    // ------------------------------------------------------------------------------------------------------------------------------

    public void initialize(SQLiteDatabase database) throws Exception {
    }

    private BinderJson packBinderJson = (statement, itemArray) -> {
        statement.bindLong(1, itemArray.getLong(0));
        statement.bindString(2, itemArray.getString(1));
    };

    // ------------------------------------------------------------------------------------------------------------------------------

    private interface BinderJson {
        void bind(SQLiteStatement statement, JSONArray itemArray) throws Exception;
    }

    private interface BinderCursor {
        void bind(SQLiteStatement statement, Cursor c);
    }

    private void executeQuery(SQLiteDatabase database, int queryResId) {
        SQLiteStatement statement = null;
        try {
            database.beginTransaction();
            statement = database.compileStatement(context.getString(queryResId));
            statement.execute();
            statement.close();
            database.setTransactionSuccessful();
        } finally {
            if (statement != null) {
                statement.close();
            }

            if (database.inTransaction()) {
                database.endTransaction();
            }
        }
    }

    // ------------------------------------------------------------------------------------------------------------------------------

    private void createBackupTables(ConnectionSource connectionSource) throws Exception {
        TableUtils.createTable(connectionSource, Backup.UserInsert.class);
        TableUtils.createTable(connectionSource, Backup.UserMobileDeviceInsert.class);
        TableUtils.createTable(connectionSource, Backup.ObdDeviceInsert.class);
        TableUtils.createTable(connectionSource, Backup.VehicleInsert.class);
        TableUtils.createTable(connectionSource, Backup.DriveHistoryInsert.class);
    }

    private void backup(SQLiteDatabase database) {
        EventBus.getDefault().post(new ProgressMessage(context.getResources().getString(R.string.message_common_progress_backup)));
        executeQuery(database, R.string.sql_backup_user_insert);
        executeQuery(database, R.string.sql_backup_user_mobile_device_insert);
        executeQuery(database, R.string.sql_backup_obd_device_insert);
        executeQuery(database, R.string.sql_backup_vehicle_insert);
        executeQuery(database, R.string.sql_backup_drive_history_insert);
    }

    private void restoreDatas(SQLiteDatabase database) {
        EventBus.getDefault().post(new ProgressMessage(context.getResources().getString(R.string.message_common_progress_restore)));
        executeQuery(database, R.string.sql_restore_user_insert);
        executeQuery(database, R.string.sql_restore_user_mobile_device_insert);
        executeQuery(database, R.string.sql_restore_obd_device_insert);
        executeQuery(database, R.string.sql_restore_vehicle_insert);
        executeQuery(database, R.string.sql_restore_drive_history_insert);
    }

    private void dropBackupTables(ConnectionSource connectionSource) throws Exception {
        EventBus.getDefault().post(new ProgressMessage(context.getResources().getString(R.string.message_common_progress_update)));
        TableUtils.dropTable(connectionSource, Backup.UserInsert.class, true);
        TableUtils.dropTable(connectionSource, Backup.UserMobileDeviceInsert.class, true);
        TableUtils.dropTable(connectionSource, Backup.ObdDeviceInsert.class, true);
        TableUtils.dropTable(connectionSource, Backup.VehicleInsert.class, true);
        TableUtils.dropTable(connectionSource, Backup.DriveHistoryInsert.class, true);
    }





    // ------------------------------------------------------------------------------------------------------------------------------

    public static class VehicleDao extends RuntimeExceptionDao<Vehicle, Long> {
        public VehicleDao(Dao<Vehicle, Long> dao) {
            super(dao);
        }
    }

    public static class ObdDeviceDao extends RuntimeExceptionDao<ObdDevice, Long> {
        public ObdDeviceDao(Dao<ObdDevice, Long> dao) {
            super(dao);
        }
    }

    public static class DriveHistoryDao extends RuntimeExceptionDao<DriveHistory, Long> {
        public DriveHistoryDao(Dao<DriveHistory, Long> dao) {
            super(dao);
        }
    }

    public static class SensingLogDao extends RuntimeExceptionDao<SensingLog, Long> {
        public SensingLogDao(Dao<SensingLog, Long> dao) {
            super(dao);
        }
    }

    public static class DtcRawDao extends RuntimeExceptionDao<DtcRaw, Long> {
        public DtcRawDao(Dao<DtcRaw, Long> dao) {
            super(dao);
        }
    }

    public static class UserDao extends RuntimeExceptionDao<User, Long> {
        public UserDao(Dao<User, Long> dao) {
            super(dao);
        }
    }

    public static class UserMobileDeviceDao extends RuntimeExceptionDao<UserMobileDevice, Long> {
        public UserMobileDeviceDao(Dao<UserMobileDevice, Long> dao) {
            super(dao);
        }
    }

    public static class FotaDao extends RuntimeExceptionDao<Fota, Long> {
        public FotaDao(Dao<Fota, Long> dao) {
            super(dao);
        }
    }

    public static class ActivityLogDao extends RuntimeExceptionDao<ActivityLog, Long> {
        public ActivityLogDao(Dao<ActivityLog, Long> dao) {
            super(dao);
        }
    }

}