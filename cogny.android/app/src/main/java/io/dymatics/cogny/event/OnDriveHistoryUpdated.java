package io.dymatics.cogny.event;

import io.dymatics.cogny.domain.model.DriveHistory;
import lombok.Getter;

@Getter
public class OnDriveHistoryUpdated {
    private DriveHistory driveHistory;
    private boolean driving;

    public OnDriveHistoryUpdated(DriveHistory driveHistory, boolean driving) {
        this.driveHistory = driveHistory;
        this.driving = driving;
    }
}
