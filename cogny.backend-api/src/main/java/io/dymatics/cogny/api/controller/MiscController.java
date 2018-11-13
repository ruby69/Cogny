package io.dymatics.cogny.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.dymatics.cogny.api.service.MiscService;

@RestController
public class MiscController {
    @Autowired private MiscService miscService;

    @RequestMapping(value = "meta", method = RequestMethod.GET)
    public Object meta() {
        return miscService.findLatestMeta();
    }

    @RequestMapping(value = "fotas/{type}", method = RequestMethod.GET)
    public Object fotas(@PathVariable String type) {
        return miscService.findLatestFotaBy(type);
    }

    @RequestMapping(value = "healthcheck", method = RequestMethod.GET)
    public Object healthcheck() {
        return "OK - 2018/12/17 16:45";
    }
}
