package io.dymatics.cogny.domain.model;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString(of = { "dtcHistoryNo", "vehicleNo", "obdDeviceNo", "dtcCode" })
@JsonInclude(Include.NON_NULL)
public class DtcHistory implements Serializable {
    private static final long serialVersionUID = -3616678126818781730L;

    private Long dtcHistoryNo;
    private Long vehicleNo;
    private Long obdDeviceNo;
    private Long driveHistoryNo;
    private Date dtcIssuedTime;
    private Date dtcDeletedTime;
    private Long deletedDriveHistoryNo;
    private String dtcCode;
    private Long repairNo;
    private Date regDate;
    private Date updDate;

    public DtcHistory(DtcRaw dtcRaw) {
        setVehicleNo(dtcRaw.getVehicleNo());
        setObdDeviceNo(dtcRaw.getObdDeviceNo());
        setDriveHistoryNo(dtcRaw.getDriveHistoryNo());
        setDtcIssuedTime(dtcRaw.getDtcIssuedTime());
        setDtcCode(dtcRaw.getDtcCode());
    }
}
