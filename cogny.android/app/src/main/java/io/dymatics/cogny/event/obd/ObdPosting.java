package io.dymatics.cogny.event.obd;

import lombok.Getter;

public class ObdPosting {
    @Getter private boolean run;

    public ObdPosting(boolean run) {
        this.run = run;
    }
}
