package io.dymatics.cogny.staff.service.impl;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.dymatics.cogny.domain.model.EnumToMap;
import io.dymatics.cogny.domain.model.Page;
import io.dymatics.cogny.domain.model.Partner;
import io.dymatics.cogny.domain.model.Partner.ContractStatus;
import io.dymatics.cogny.domain.model.Partner.PartnerType;
import io.dymatics.cogny.domain.persist.PartnerRepository;
import io.dymatics.cogny.staff.service.PartnerService;

@Service
@Transactional(readOnly = true)
public class PartnerServiceImpl implements PartnerService {
    @Autowired private PartnerRepository partnerRepository;

    @Override
    public Object populatePartners(Page page) {
        page.setTotal(partnerRepository.countByPage(page));
        page.setContents(partnerRepository.findByPage(page));
        return page;
    }

    @Override
    public Object getPartner(Long partnerNo) {
        return partnerRepository.findByNo(partnerNo);
    }

    @Override
    public Object getPartnerEnums() {
        Map<String, Object> returnEnums = new HashMap<String, Object>();
        returnEnums.put("partnerTypeEnums", EnumToMap.getEnumList(PartnerType.values()));
        returnEnums.put("partnerStatusEnums", EnumToMap.getEnumList(ContractStatus.values()));
        return returnEnums;
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public Object savePartner(Partner partner) {
        if (partner.getPartnerNo() != null) {
            partnerRepository.update(partner);
        } else {
            partner.setPartnerCode(generatePartnerCode());
            partnerRepository.insert(partner);
        }
        return getPartner(partner.getPartnerNo());
    }

    private String generatePartnerCode() {
        Set<String> codes = partnerRepository.findAllPartnerCodes();
        String code = null;
        do {
            code = RandomStringUtils.randomAlphanumeric(5).toUpperCase(Locale.getDefault());
        } while(codes.contains(code));
        return code;
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public Object deletePartner(Long partnerNo) {
        partnerRepository.delete(partnerNo);
        return getPartner(partnerNo);
    }

}
