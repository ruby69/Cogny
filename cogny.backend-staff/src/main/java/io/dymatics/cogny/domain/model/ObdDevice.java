package io.dymatics.cogny.domain.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.dymatics.cogny.domain.model.EnumToMap.EnumValuable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class ObdDevice implements Serializable {
    private static final long serialVersionUID = 2307189840186827559L;
    public enum Status implements EnumValuable {
        BUSY("설치중"), FREE("미설치");
        private final String name;
        private Status(String name) {
            this.name = name;
        }
        @Override
        public String getName() {
            return name;
        }
        public boolean isFree() {
            return this == FREE;
        }
        public boolean isBusy() {
            return this == BUSY;
        }
    }
    private Long obdDeviceNo;
    private String obdSerial;
    private Long partnerNo;
    private Status status;
    private String statusName;
    @JsonIgnore private Boolean enabled;
    @JsonIgnore private Long regUserNo;
    private Date regDate;
    private Date updDate;
    @JsonIgnore private Date delDate;

    private Vehicle vehicle;
    private Partner partner;
    private Long obdDeviceVehicleNo;
    private Date obdInstallDate;

    public ObdDevice(Long obdDeviceNo, String obdSerial, Long partnerNo, Status status) {
        this.obdDeviceNo = obdDeviceNo;
        this.obdSerial = obdSerial;
        this.partnerNo = partnerNo;
        this.status = status;
    }

    public ObdDevice(Long obdDeviceNo, String obdSerial) {
        this(obdDeviceNo, obdSerial, null, Status.FREE);
    }

    public ObdDevice(Long obdDeviceNo, String obdSerial, Long partnerNo) {
        this(obdDeviceNo, obdSerial, partnerNo, Status.FREE);
    }

    public ObdDevice(String obdSerial, Long partnerNo) {
        this(null, obdSerial, partnerNo, Status.FREE);
    }

    @JsonIgnore
    public boolean isFree() {
        return status.isFree();
    }

    @JsonIgnore
    public boolean isBusy() {
        return status.isBusy();
    }

    public void setStatus(Status status) {
        this.status = status;
        this.statusName = status.getName();
    }

    @Data
    @JsonInclude(Include.NON_NULL)
    public static class Form implements Serializable {
        private static final long serialVersionUID = 7830857130537705007L;

        private Long obdDeviceNo;
        private String obdSerial;
        private Long partnerNo;

        private Long vehicleNo;
        @JsonIgnore private User currentUser;

        private List<ObdDevice> obdDeviceList;

        public ObdDevice getObdDevice() {
            return new ObdDevice(obdDeviceNo, obdSerial, partnerNo);
        }
    }
}
