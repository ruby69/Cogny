package io.dymatics.cognyreport.event;

import io.dymatics.cognyreport.domain.model.DtcReport;
import lombok.Getter;

public class OnChangedDtcReport {
    @Getter private DtcReport dtcReport;

    public OnChangedDtcReport(DtcReport dtcReport) {
        this.dtcReport = dtcReport;
    }
}
