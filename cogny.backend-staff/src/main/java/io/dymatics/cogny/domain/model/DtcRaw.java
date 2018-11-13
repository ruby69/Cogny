package io.dymatics.cogny.domain.model;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class DtcRaw implements Serializable {
    private static final long serialVersionUID = 6298029376030615971L;

    private Long dtcRawNo;
    private Long vehicleNo;
    private Long obdDeviceNo;
    private Long driveHistoryNo;
    private Long dtcSeq;
    private Date dtcIssuedTime;
    private Date dtcUpdatedTime;
    private String dtcCode;
    private String desc;
    private String dtcState;
    private Date regDate;

    private Vehicle vehicle;
    private ObdDevice obdDevice;
    private DriveHistory driveHistory;
    
    public boolean isCtr() {
        if(!StringUtils.isEmpty(this.dtcCode) && this.dtcCode.substring(0, 1).equals("P")) {
            return true;
        }
        return false;
    }
    
    public boolean isStateCurrent() {
        /*
         *   bit at 7 : dtcState.substring(3, 4)
         *   bit at 6 : dtcState.substring(4, 5)
         *   bit at 5 : dtcState.substring(5, 6)
         *   bit at 4 : dtcState.substring(6, 7)
         *   bit at 3 : dtcState.substring(7, 8)
         *   bit at 2 : dtcState.substring(8, 9)
         *   bit at 1 : dtcState.substring(9, 10)
         *   bit at 0 : dtcState.substring(10, 11)
         *
         * 6번 bit와 7번 bit가 1인 경우 dtc '현재'로 처리
         *
         * */
        if("1".equals(dtcState.substring(4, 5)) && "1".equals(dtcState.substring(5, 6))) {
            return true;
        }
        return false;
    }
}
