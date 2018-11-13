package io.dymatics.cogny.domain.model;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class ObdDeviceVehicle implements Serializable {
    private static final long serialVersionUID = 8645153436766469654L;

    private Long obdDeviceVehicleNo;
    private Long obdDeviceNo;
    private Long vehicleNo;
    private Date installDate;
    private Date uninstallDate;
    private Date regDate;
    private Date updDate;

    private Vehicle vehicle;
    private ObdDevice obdDevice;

    public ObdDeviceVehicle(Long obdDeviceNo, Long vehicleNo, Date installDate) {
        this.obdDeviceNo = obdDeviceNo;
        this.vehicleNo = vehicleNo;
        this.installDate = installDate;
    }
}
