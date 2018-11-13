package io.dymatics.cogny.domain.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(Include.NON_NULL)
public class DriveHistory implements Serializable {
    private static final long serialVersionUID = -5087814199684687092L;

    private Long driveHistoryNo;
    private Long vehicleNo;
    private Long obdDeviceNo;
    private Long userNo;
    private Long userMobileDeviceNo;

    private String startDate;
    private String startTime;
    private Double startLatitude;
    private Double startLongitude;
    private String startAddress;
    private Long startMileage;

    private Date endTime;
    private Double endLatitude;
    private Double endLongitude;
    private String endAddress;
    private Long endMileage;

    private Long driveDistance;
    private Long driveFuelMileage;
    private Long driveFuelConsumption;

    private Date regDate;
    private Date updDate;

    private Vehicle vehicle;
    private ObdDevice obdDevice;
    private User user;
    private DriveHistoryMemo driveHistoryMemo;

    private List<DtcRaw> dtcRawList;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(Include.NON_NULL)
    public static class DriveHistoryMemo implements Serializable {
        private static final long serialVersionUID = -5271517959438745368L;
        
        private Long driveHistoryMemoNo;
        private Long driveHistoryNo;
        private String memo;
        private Long regUserNo;
        @JsonIgnore private Boolean enabled;
        private Date regDate;
        private Date updDate;
        @JsonIgnore private Date delDate;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(Include.NON_NULL)
    public static class DriveRepairLog implements Serializable {
        private static final long serialVersionUID = 2445006446105205078L;
        
        private Long driveRepairLogNo;
        private Long vehicleNo;
        private Long userNo;
        private Long driveTime;
        private Long driveMileage;
        private Long totalMileage;
        private DriveRepairLogRef ref;
        private Long refNo;
        private Long dateIdx;
        private String startDate;

        public enum DriveRepairLogRef {
            DRIVE, REP, REPM;
        }
        
        
    }
}
