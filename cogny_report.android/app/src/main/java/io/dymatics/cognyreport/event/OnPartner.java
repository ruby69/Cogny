package io.dymatics.cognyreport.event;

import io.dymatics.cognyreport.domain.model.Partner;
import lombok.Getter;

public class OnPartner {
    @Getter private Partner partner;

    public OnPartner(Partner partner) {
        this.partner = partner;
    }
}
