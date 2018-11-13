package io.dymatics.cogny.domain.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString(of = {"activitySeq", "category", "activity"})
@DatabaseTable(tableName = "ActivityLog")
public class ActivityLog implements Serializable {
    private static final long serialVersionUID = -416078435139442591L;

    public static final String FIELD_pk = "activitySeq";
    public static final String FIELD_uploaded = "uploaded";

    public enum Category {
        APP, SIGN, JOB, BT, OBD, ETC
    }

    @DatabaseField(columnName = FIELD_pk, generatedId = true) private Long activitySeq;
    @DatabaseField private Long userNo;
    @DatabaseField private Long mobileDeviceNo;
    @DatabaseField private Long vehicleNo;
    @DatabaseField private Long obdDeviceNo;
    @DatabaseField private Long driveHistoryNo;
    @DatabaseField private Category category;
    @DatabaseField private String activity;
    @DatabaseField private long activityTime;
    @DatabaseField(defaultValue = "false") private boolean uploaded;

    public String[] toArray() {
        String[] array = new String[9];
        array[0] = String.valueOf(userNo);
        array[1] = String.valueOf(activitySeq);
        array[2] = String.valueOf(mobileDeviceNo);
        array[3] = String.valueOf(vehicleNo);
        array[4] = String.valueOf(obdDeviceNo);
        array[5] = String.valueOf(driveHistoryNo);
        array[6] = category.name();
        array[7] = activity;
        array[8] = String.valueOf(activityTime);
        return array;
    }


    public enum Code {
        APP10001,
        APP10002,
        APP10003,
        APP10004,
        APP10005,

        JOB10001,
        JOB10101,
        JOB10102,
        JOB10201,
        JOB10202,
        JOB10301,
        JOB10302,
        JOB10303,
        JOB10401,
        JOB10402,
        JOB10403,

        OBD10001,
        OBD10002,
        OBD10003,
        OBD10004,
        OBD10005,
        OBD10101,
        OBD10102,
        OBD10103,

        ETC10101,
        ETC10102,
        ETC10103,
        ETC10201,
        ETC10202,
        ETC10203,
        ETC10204,
        ETC10205,
        ETC10206,
        ETC10301,
        ETC10302,

        BT10001,
        BT10002,
        BT10003,
        BLE10001,
        BLE10002,
        BLE10003,
        BLE10004,
        BLE10005,
        BLE10006,
        BLE10007,
        BLE10008,
        BLE10009,
        BLE10010,
        BLE10011
    }
}
