package io.dymatics.cogny.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString(of = {"driveHistoryNo"})
@DatabaseTable(tableName = "DriveHistory")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DriveHistory implements Serializable {
    private static final long serialVersionUID = -2575765705164126626L;

    public static final String FIELD_pk = "driveHistoryNo";

    @DatabaseField(columnName = FIELD_pk, id = true) private Long driveHistoryNo;
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

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Summary implements Serializable {
        private static final long serialVersionUID = 4023942218199557710L;

        private String driveDate;
        private String driveTime;
        private long distance;
        private long odometer;
        private String message;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Summaries extends ArrayList<Summary> {
        private static final long serialVersionUID = 8066703597506535823L;

    }
}
