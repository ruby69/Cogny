package io.dymatics.cogny.domain.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class LinkForm implements Serializable {
    private static final long serialVersionUID = -2305276834131046283L;

    public enum Perspective {
        VEHICLE, OBD;

        public boolean isVehicle() {
            return this == VEHICLE;
        }

        public boolean isObd() {
            return this == OBD;
        }
    }

    private Long obdDeviceVehicleNo;
    private Long vehicleNo;
    private Long obdDeviceNo;
    private Long partnerNo;
    private String obdSerial;
    private Perspective perspective;

    public boolean isSetup() {
        return obdDeviceVehicleNo == null && obdDeviceNo != null && vehicleNo != null;
    }

    public boolean isModify() {
        if (perspective.isVehicle()) {
            return obdDeviceVehicleNo != null && obdDeviceNo != null && vehicleNo == null;
        } else {
            return obdDeviceVehicleNo != null && obdDeviceNo == null && vehicleNo != null;
        }
    }

    public boolean isRelease() {
        return obdDeviceVehicleNo != null && obdDeviceNo == null && vehicleNo == null;
    }

    public ObdDevice getObdDevice() {
        return new ObdDevice(obdDeviceNo, obdSerial, partnerNo);
    }
}
