package io.dymatics.cogny.jobs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.dymatics.cogny.jobs.service.ScheduleService;

@RestController
@Profile(value = { "product", "test" })
public class ScheduleController {
    @Autowired private ScheduleService scheduleService;

    @RequestMapping(value = "jobs", method = RequestMethod.GET)
    public Object scheduledJob() {
        try {
            scheduleService.scheduledJob();
            return "OK";
        } catch (Exception e) {
            return "NOT OK";
        }
    }

    @RequestMapping(value = "healthcheck", method = RequestMethod.GET)
    public Object healthcheck() {
        return "OK - 2018/12/18 14:45";
    }
}
