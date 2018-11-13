package io.dymatics.cogny.staff.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.dymatics.cogny.domain.model.CognyStatus;
import io.dymatics.cogny.domain.model.SensingRaw;
import io.dymatics.cogny.domain.model.User;
import io.dymatics.cogny.staff.service.SensingRawService;
import io.dymatics.cogny.staff.service.VehicleService;

@RestController
public class SensorDataController {
    @Autowired private SensingRawService sensorDataService;

    @Autowired private VehicleService vehicleService;

    @RequestMapping(value = "sensordata/raw", method = RequestMethod.POST)
    public Object getSensingRaw(@AuthenticationPrincipal User currentUser, @RequestBody SensingRaw.Form form) throws Exception {
        if(vehicleService.checkAuthority(currentUser, form.getVehicleNo())) {
            form.addDefaultFields();
            form.setColomnList();
            return sensorDataService.getSensingRaw(form);
        } else {
            return CognyStatus.ACCESS_DENYED.toEntityMap();
        }
    }
}
