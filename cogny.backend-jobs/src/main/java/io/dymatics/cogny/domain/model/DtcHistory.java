package io.dymatics.cogny.domain.model;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class DtcHistory implements Serializable {
    private static final long serialVersionUID = 5186870599432121375L;
    
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

}
