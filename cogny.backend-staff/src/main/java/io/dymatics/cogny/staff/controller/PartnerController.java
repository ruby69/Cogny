package io.dymatics.cogny.staff.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.dymatics.cogny.domain.model.Page;
import io.dymatics.cogny.domain.model.Partner;
import io.dymatics.cogny.staff.service.PartnerService;

@RestController
public class PartnerController {
    @Autowired private PartnerService partnerService;

    @RequestMapping(value = "partners", method = RequestMethod.GET)
    public Object getList(Page page) {
        return partnerService.populatePartners(page);
    }

    @RequestMapping(value = "partners/{partnerNo}", method = RequestMethod.GET)
    public Object getDetail(@PathVariable Long partnerNo) {
        return partnerService.getPartner(partnerNo);
    }

    @RequestMapping(value = "partners/enums", method = RequestMethod.GET)
    public Object getEnums() {
        return partnerService.getPartnerEnums();
    }

    @RequestMapping(value = "partners", method = { RequestMethod.POST, RequestMethod.PUT })
    public Object save(@RequestBody Partner.Form form) {
        return partnerService.savePartner(form.getPartner());
    }

    @RequestMapping(value = "partners/{partnerNo}", method = RequestMethod.DELETE)
    public Object delete(@PathVariable Long partnerNo) {
        return partnerService.deletePartner(partnerNo);
    }
}