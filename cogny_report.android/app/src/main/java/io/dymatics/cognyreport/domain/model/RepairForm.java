package io.dymatics.cognyreport.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RepairForm implements Serializable {
    private static final long serialVersionUID = 7994383624354844683L;

    private Long vehicleNo;
    private Long repairMsgNo;
    private int odometer;
    private Long recvUserMobileDeviceNo;

    public static RepairForm instance(DtcReport dtcReport) {
        RepairForm form = new RepairForm();
        form.setVehicleNo(dtcReport.getVehicleNo());
        RepairMsg repairMsg = dtcReport.getRepairMsg();
        if (repairMsg != null) {
            form.setRepairMsgNo(repairMsg.getRepairMsgNo());
        }
        form.setOdometer(dtcReport.getOdometer());
        form.setRecvUserMobileDeviceNo(dtcReport.getUserMobileDevice().getUserMobileDeviceNo());
        return form;
    }
}
