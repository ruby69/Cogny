package io.dymatics.cogny.event;

import io.dymatics.cogny.domain.model.ActivityLog;
import lombok.Getter;

public class OnActivity {
    @Getter private ActivityLog.Category category;
    @Getter private Object[] values;

    public OnActivity(ActivityLog.Category category, Object...values) {
        this.category = category;
        this.values = values;
    }
}
