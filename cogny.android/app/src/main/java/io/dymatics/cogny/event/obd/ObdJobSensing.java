package io.dymatics.cogny.event.obd;

import lombok.Getter;

public class ObdJobSensing {
    @Getter private boolean run;

    public ObdJobSensing(boolean run) {
        this.run = run;
    }
}
