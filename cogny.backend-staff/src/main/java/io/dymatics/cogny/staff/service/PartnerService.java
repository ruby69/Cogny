package io.dymatics.cogny.staff.service;

import io.dymatics.cogny.domain.model.Page;
import io.dymatics.cogny.domain.model.Partner;

public interface PartnerService {
    Object populatePartners(Page page);

    Object getPartner(Long partnerNo);

    Object getPartnerEnums();

    Object savePartner(Partner partner);

    Object deletePartner(Long partnerNo);
}
