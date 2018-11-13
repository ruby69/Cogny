package io.dymatics.cogny.api.controller;

import java.util.List;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.dymatics.cogny.api.service.LogService;

@RestController
public class LogController {
    @Autowired private LogService logService;
    @Value("#{taskExecutor}") private ThreadPoolTaskExecutor taskExecutor;

    @RequestMapping(value = "activities", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Future<Void> activities(@RequestBody List<String> logs) {
        return taskExecutor.submit(() -> {
            logService.saveActivityLogs(logs);
            return null;
        });
    }
}
