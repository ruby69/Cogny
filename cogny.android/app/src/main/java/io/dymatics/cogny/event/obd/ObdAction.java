package io.dymatics.cogny.event.obd;

import io.dymatics.cogny.support.obd.Protocol;
import lombok.Getter;

public class ObdAction {
    @Getter private Protocol.Cmd.Write cmd;
    private byte[] payload;
    private int cCount;
    private int tCount;

    public ObdAction(Protocol.Cmd.Write cmd) {
        this.cmd = cmd;
    }

    public ObdAction(Protocol.Cmd.Write cmd, byte[] payload) {
        this.cmd = cmd;
        this.payload = payload;
    }

    public ObdAction(Protocol.Cmd.Write cmd, byte[] payload, int cCount, int tCount) {
        this.cmd = cmd;
        this.payload = payload;
        this.cCount = cCount;
        this.tCount = tCount;
    }

    public byte[] getCmdBytes() {
        if (payload != null) {
            if (tCount > 0) {
                return Protocol.Cmd.Write.getBytes(cmd, payload, cCount, tCount);
            } else {
                return Protocol.Cmd.Write.getBytes(cmd, payload);
            }
        } else {
            return Protocol.Cmd.Write.getBytes(cmd);
        }
    }
}
