package io.dymatics.cogny.domain.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

public class Backup {
    @Data
    @DatabaseTable(tableName = "UserInsert")
    public static class UserInsert implements Serializable {
        private static final long serialVersionUID = -4769166431221745139L;

        @DatabaseField(columnName = User.FIELD_pk, id = true) private Long userNo;
        @DatabaseField private Long partnerNo;
        @DatabaseField private String signProvider;
        @DatabaseField private String email;
        @DatabaseField private String name;
        @DatabaseField private String hpNo;
    }

    @Data
    @DatabaseTable(tableName = "UserMobileDeviceInsert")
    public static class UserMobileDeviceInsert implements Serializable {
        private static final long serialVersionUID = 5221683325988305863L;

        @DatabaseField(columnName = UserMobileDevice.FIELD_pk, id = true) private Long userMobileDeviceNo;
    }

    @Data
    @DatabaseTable(tableName = "ObdDeviceInsert")
    public static class ObdDeviceInsert implements Serializable {
        private static final long serialVersionUID = 3491481436355878984L;

        @DatabaseField(columnName = ObdDevice.FIELD_pk, id = true) private Long obdDeviceNo;
        @DatabaseField private String obdSerial;
        @DatabaseField private Date regDate;
    }

    @Data
    @DatabaseTable(tableName = "VehicleInsert")
    public static class VehicleInsert implements Serializable {
        private static final long serialVersionUID = 8999635893988813371L;

        @DatabaseField(columnName = Vehicle.FIELD_pk, id = true) private Long vehicleNo;
        @DatabaseField private Long modelNo;
        @DatabaseField private Long fuelNo;
        @DatabaseField private String licenseNo;
        @DatabaseField private int modelYear;
        @DatabaseField private int modelMonth;
        @DatabaseField private String regDate;

        @DatabaseField private String modelName;
        @DatabaseField private String fuelName;
        @DatabaseField private Long manufacturerNo;
        @DatabaseField private String manufacturerName;
    }

    @Data
    @DatabaseTable(tableName = "DriveHistoryInsert")
    public static class DriveHistoryInsert implements Serializable {
        private static final long serialVersionUID = -1977325494482207235L;

        @DatabaseField(columnName = DriveHistory.FIELD_pk, id = true) private Long driveHistoryNo;
        @DatabaseField private Long vehicleNo;
        @DatabaseField private Long obdDeviceNo;
        @DatabaseField private Long userNo;
        @DatabaseField private Long userMobileDeviceNo;

        @DatabaseField private Date startDate;
        @DatabaseField private String startTime;
        @DatabaseField private double startLatitude;
        @DatabaseField private double startLongitude;
        @DatabaseField private String startAddress;
        @DatabaseField private int startMileage;

        @DatabaseField private Date endTime;
        @DatabaseField private double endLatitude;
        @DatabaseField private double endLongitude;
        @DatabaseField private String endAddress;
        @DatabaseField private int endMileage;

        @DatabaseField private int driveDistance;
    }

}
