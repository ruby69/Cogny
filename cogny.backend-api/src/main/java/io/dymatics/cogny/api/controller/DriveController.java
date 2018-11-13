package io.dymatics.cogny.api.controller;

import java.util.List;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.dymatics.cogny.api.service.DriveService;
import io.dymatics.cogny.domain.model.DriveHistory;
import io.dymatics.cogny.domain.model.DriveRepairLog;
import io.dymatics.cogny.domain.model.DtcRaw;
import io.dymatics.cogny.domain.model.SensingLog;
import io.dymatics.cogny.domain.model.Sensings;
import io.dymatics.cogny.domain.model.User;

@RestController
public class DriveController {
    @Autowired private DriveService driveService;
    @Value("#{taskExecutor}") private ThreadPoolTaskExecutor taskExecutor;

    @RequestMapping(value = "drives", method = { RequestMethod.POST, RequestMethod.PUT })
    public Object drives(@RequestBody DriveHistory driveHistory, User user) {
        driveHistory.setUserNo(user.getUserNo());
        return driveService.saveStart(driveHistory);
    }

    @RequestMapping(value = "drivef", method = { RequestMethod.POST, RequestMethod.PUT })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void drivef(@RequestBody DriveHistory driveHistory) {
        driveService.saveEnd(driveHistory);
    }

    @RequestMapping(value = "sensing", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Future<Void> sensing(@RequestBody Sensings sensings) {
        return taskExecutor.submit(() -> {
            driveService.saveSensings(sensings);
            return null;
        });
    }

    @RequestMapping(value = "messages/{vehicleNo}", method = RequestMethod.GET)
    public Object messages(@PathVariable("vehicleNo") Long vehicleNo) {
        return driveService.repairMessage(vehicleNo);
    }

    @RequestMapping(value = "histories/{vehicleNo}", method = RequestMethod.GET)
    public Object histories(@PathVariable("vehicleNo") Long vehicleNo, DriveRepairLog.Page page, User user) {
        page.param("vehicleNo", vehicleNo);
        page.param("userNo", user.getUserNo());
        return driveService.populateHistories(page).clear();
    }








    @SuppressWarnings("deprecation")
    @RequestMapping(value = { "sensings", "sensinglogs" }, method = { RequestMethod.POST, RequestMethod.PUT })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Deprecated
    public Future<Void> sensings(@RequestBody List<SensingLog> sensingLogs) {
        return taskExecutor.submit(() -> {
            driveService.saveSensingRawOld(sensingLogs);
            return null;
        });
    }

    @SuppressWarnings("deprecation")
    @RequestMapping(value = "dtcraws", method = { RequestMethod.POST, RequestMethod.PUT })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Deprecated
    public Future<Void> dtcraw(@RequestBody List<DtcRaw> dtcRaws) {
        return taskExecutor.submit(() -> {
            driveService.saveDtcRawsOld(dtcRaws);
            return null;
        });
    }

}
