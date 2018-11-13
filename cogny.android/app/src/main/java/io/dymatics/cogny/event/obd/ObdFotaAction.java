package io.dymatics.cogny.event.obd;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public class ObdFotaAction {
    @Getter private List<ObdAction> actions;

    public ObdFotaAction(){}

    public ObdFotaAction(List<ObdAction> actions) {
        this.actions = actions;
    }

    public ObdFotaAction add(ObdAction obdAction) {
        if (actions == null) {
            actions = new ArrayList<>();
        }
        actions.add(obdAction);
        return this;
    }
}
