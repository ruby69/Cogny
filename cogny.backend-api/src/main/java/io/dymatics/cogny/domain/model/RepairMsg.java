package io.dymatics.cogny.domain.model;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RepairMsg implements Serializable {
    private static final long serialVersionUID = -1105240580524815401L;

    public enum Msg {
        NORMAL("운행 종료 후 간단한 점검이 필요합니다."),
        EMERGENCY("점검을 위해 즉시 복귀하세요."),
        EMPTY(""),
        COMPLETE("점검이 완료되었습니다.");

        @Getter String message;
        Msg(String message) {
            this.message = message;
        }

        public boolean isEmergency() {
            return this == EMERGENCY;
        }

        public boolean isEmpty() {
            return this == EMPTY;
        }
    }
    public enum Call {MSG, CALL}
    public enum Status {VISIBLE, INVISIBLE, COMPLETE}

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
        private static final long serialVersionUID = -3561363661286312449L;

        private Long vehicleNo;
        private Long recvUserMobileDeviceNo;
        private Long sendUserNo;
        private RepairMsg.Msg msgType;
        private RepairMsg.Call callType;

        public RepairMsg getRepairMsg() {
            RepairMsg repairMsg = new RepairMsg();
            repairMsg.setVehicleNo(vehicleNo);
            repairMsg.setUserNo(sendUserNo);
            repairMsg.setMsgType(msgType);
            repairMsg.setCallType(callType);
            return repairMsg;
        }
    }

    @Data
    @NoArgsConstructor
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Flag implements Serializable {
        private static final long serialVersionUID = -2904095551788585560L;

        private boolean completed;
        private boolean needed;
    }
}
