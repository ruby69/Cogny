package io.dymatics.cogny.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RepairMsg implements Serializable {
    private static final long serialVersionUID = -1105240580524815401L;

    public enum Status {VISIBLE, INVISIBLE, COMPLETE}
    public enum Msg {
        NORMAL("운행 종료 후 간단한 점검이 필요합니다."),
        EMERGENCY("점검을 위해 즉시 복귀하세요."),
        EMPTY(""),
        COMPLETE("점검이 완료되었습니다.");

        @Getter
        String message;
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

        public boolean isComplete() {
            return this == COMPLETE;
        }
    }

    private Long repairMsgNo;
    private Long repairNo;
    private Long vehicleNo;
    private Msg msgType;
    private Status status;
    private Date regDate;

}
