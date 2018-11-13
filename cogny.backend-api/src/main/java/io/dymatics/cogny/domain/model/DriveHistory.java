package io.dymatics.cogny.domain.model;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang3.time.FastDateFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DriveHistory implements Serializable {
    private static final long serialVersionUID = -4046954333367348146L;

    private Long driveHistoryNo;
    private Long vehicleNo;
    private Long obdDeviceNo;
    private Long userNo;
    private Long userMobileDeviceNo;

    private Date startDate;
    private String startTime;
    private double startLatitude;
    private double startLongitude;
    private String startAddress;
    private int startMileage;

    private Date endTime;
    private double endLatitude;
    private double endLongitude;
    private String endAddress;
    private int endMileage;

    private int driveDistance;

    @JsonIgnore private Date regDate;
    @JsonIgnore private Date updDate;

    private static final FastDateFormat DF1 = FastDateFormat.getInstance("yyyy-MM-dd ", TimeZone.getDefault(), Locale.getDefault());
    private static final FastDateFormat DF2 = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss", TimeZone.getDefault(), Locale.getDefault());

    public Date getStartDateTime() {
        try {
            return DF2.parse(DF1.format(startDate) + startTime);
        } catch (ParseException e) {
            return null;
        }
    }

    public String getDriveDate() {
        return DF1.format(startDate);
    }

    @Data
    @NoArgsConstructor
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Summary implements Serializable {
        private static final long serialVersionUID = 4023942218199557710L;

        private String driveDate;
        private String driveTime;
        private long distance;
        private long odometer;
        private String message;

        public Summary(String driveDate) {
            this.driveDate = driveDate;
        }
    }
}
