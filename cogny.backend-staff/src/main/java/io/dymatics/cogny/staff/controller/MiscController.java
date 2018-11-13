package io.dymatics.cogny.staff.controller;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.dymatics.cogny.staff.service.MiscService;

@RestController
public class MiscController {
    @Autowired private MiscService miscService;

    private static final String serverName = RandomStringUtils.randomAlphabetic(8);

    @RequestMapping(value = "server", method = RequestMethod.GET)
    public Object serverName() {
        return serverName;
    }

    @RequestMapping(value = "healthcheck", method = RequestMethod.GET)
    public Object healthcheck() {
        return "OK - 2018/12/18 15:00";
    }

    @RequestMapping(value = "fuels", method = RequestMethod.GET)
    public Object fuels() {
        return miscService.getFuels();
    }

    @RequestMapping(value = "manufacturers", method = RequestMethod.GET)
    public Object manufacturers() {
        return miscService.getManufacturers();
    }

    @RequestMapping(value = "modelgroups/{manufacturerNo}", method = RequestMethod.GET)
    public Object modelGroups(@PathVariable Long manufacturerNo) {
        return miscService.getModelGouprs(manufacturerNo);
    }

    @RequestMapping(value = "models/{modelGroupNo}", method = RequestMethod.GET)
    public Object models(@PathVariable Long modelGroupNo) {
        return miscService.getModels(modelGroupNo);
    }

    @RequestMapping(value = "model/salesyear/{modelNo}", method = RequestMethod.GET)
    public Object modelSalesYear(@PathVariable Long modelNo) {
        return miscService.getModelSalesYear(modelNo);
    }
}
