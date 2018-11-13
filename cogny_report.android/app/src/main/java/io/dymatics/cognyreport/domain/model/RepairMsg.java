package io.dymatics.cognyreport.domain.model;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.dymatics.cognyreport.Constants;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RepairMsg implements Serializable {
    private static final long serialVersionUID = 1941903108969557831L;

    public enum Call {MSG, CALL}
    public enum Status {VISIBLE, INVISIBLE, COMPLETE}
    public enum Msg {
        NORMAL("운행 종료 후 간단한 점검이 필요합니다."),
        EMERGENCY("점검을 위해 즉시 복귀하세요."),
        EMPTY("");

        @Getter String message;
        Msg(String message) {
            this.message = message;
        }

        public boolean isEmpty() {
            return this == EMPTY;
        }

        public boolean isEmergency() {
            return this == EMERGENCY;
        }

        public boolean isNormal() {
            return this == NORMAL;
        }
    }

    private Long repairMsgNo;
    private Long repairNo;
    private Long vehicleNo;
    private Long userNo;
    private Msg msgType;
    private Call callType;
    private Status status;
    private Date regDate;

    @Data
    @NoArgsConstructor
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Form implements Serializable {
        private static final long serialVersionUID = 4660954433277232892L;

        private Long vehicleNo;
        private Long recvUserMobileDeviceNo;
        private Msg msgType;
        private Call callType;

        public static Form instance(DtcReport dtcReport, Msg msgType, Call callType) {
            RepairMsg.Form form = new RepairMsg.Form();
            form.setVehicleNo(dtcReport.getVehicleNo());
            form.setRecvUserMobileDeviceNo(dtcReport.getUserMobileDevice().getUserMobileDeviceNo());
            form.setMsgType(msgType);
            form.setCallType(callType);
            return form;
        }
    }
}
