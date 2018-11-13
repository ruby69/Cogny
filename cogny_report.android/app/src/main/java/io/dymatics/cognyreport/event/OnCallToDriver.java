package io.dymatics.cognyreport.event;

import io.dymatics.cognyreport.domain.model.DtcReport;
import lombok.Getter;

public class OnCallToDriver {
    @Getter private DtcReport dtcReport;

    public OnCallToDriver(DtcReport dtcReport) {
        this.dtcReport = dtcReport;
    }
}
