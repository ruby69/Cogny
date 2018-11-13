package io.dymatics.cogny.event.obd;

import lombok.Data;

@Data
public class ObdMode {

    public enum Mode {
        DATA, FOTA
    }

    private Mode mode;

    public ObdMode(Mode mode) {
        this.mode = mode;
    }

    public boolean isFota() {
        return mode == Mode.FOTA;
    }

    public boolean isData() {
        return mode == Mode.DATA;
    }
}
