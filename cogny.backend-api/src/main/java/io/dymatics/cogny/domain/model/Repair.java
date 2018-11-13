package io.dymatics.cogny.domain.model;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Repair implements Serializable {
    private static final long serialVersionUID = -5145779656807192749L;

    private Long repairNo;
    private Long vehicleNo;
    private Long userNo;
    private Long repairShopNo;
    private int odometer;
    private Date repairDate;
    private String memo;
    private boolean enabled;
    private Date regDate;
    private Date updDate;
    private Date delDate;

    @Data
    @NoArgsConstructor
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Form implements Serializable {
        private static final long serialVersionUID = -1303712168552143991L;

        private Long vehicleNo;
        private Long recvUserMobileDeviceNo;
        private Long repairMsgNo;
        private Long repairUserNo;
        private int odometer;

        public Repair getRepair() {
            Repair repair = new Repair();
            repair.setVehicleNo(vehicleNo);
            repair.setUserNo(repairUserNo);
            repair.setOdometer(odometer);
            return repair;
        }
    }
}
