package io.dymatics.cognyreport.event;

import io.dymatics.cognyreport.domain.model.DtcReport;
import lombok.Getter;

public class OnCompleteRepair {
    @Getter private DtcReport dtcReport;

    public OnCompleteRepair(DtcReport dtcReport) {
        this.dtcReport = dtcReport;
    }
}
