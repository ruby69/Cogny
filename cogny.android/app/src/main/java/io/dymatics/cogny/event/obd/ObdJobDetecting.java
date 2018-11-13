package io.dymatics.cogny.event.obd;

import lombok.Getter;

public class ObdJobDetecting {
    @Getter private boolean run;

    public ObdJobDetecting(boolean run) {
        this.run = run;
    }
}
