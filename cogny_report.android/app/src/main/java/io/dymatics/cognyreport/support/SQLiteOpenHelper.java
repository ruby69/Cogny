package io.dymatics.cognyreport.support;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import org.greenrobot.eventbus.EventBus;

import io.dymatics.cognyreport.BuildConfig;
import io.dymatics.cognyreport.R;
import io.dymatics.cognyreport.domain.model.User;
import io.dymatics.cognyreport.domain.model.UserMobileDevice;
import io.dymatics.cognyreport.event.OnCompleteOrmLite;

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

//            createBackupTables(connectionSource);
//            backup(database);

            dropAndCreateTables(connectionSource);
            initialize(database);

//            restoreDatas(database);
//            dropBackupTables(connectionSource);

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
        TableUtils.dropTable(connectionSource, UserMobileDevice.class, true);
        TableUtils.dropTable(connectionSource, User.class, true);

        TableUtils.createTable(connectionSource, User.class);
        TableUtils.createTable(connectionSource, UserMobileDevice.class);
    }

    public void initialize(SQLiteDatabase database) throws Exception {
    }






    // ------------------------------------------------------------------------------------------------------------------------------



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
}