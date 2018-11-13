package io.dymatics.cogny.event;

import io.dymatics.cogny.domain.model.RepairMsg;
import lombok.Getter;

public class OnNotiMessage {
    @Getter private RepairMsg.Msg msg;

    public OnNotiMessage(RepairMsg.Msg msg) {
        this.msg = msg;
    }
}
