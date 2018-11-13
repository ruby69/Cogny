package io.dymatics.cogny.staff.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.dymatics.cogny.domain.model.CognyStatus;
import io.dymatics.cogny.domain.model.LinkForm;
import io.dymatics.cogny.domain.model.ObdDevice;
import io.dymatics.cogny.domain.model.User;
import io.dymatics.cogny.staff.service.LinkService;
import io.dymatics.cogny.staff.service.ObdDeviceService;

@RestController
public class LinkController {
    @Autowired private LinkService linkService;
    @Autowired private ObdDeviceService obdDeviceService;
    
// 사용안하는 듯
//    @RequestMapping(value = "linkobd", method = {RequestMethod.POST, RequestMethod.PUT})
//    public Object linkVehicleObd(@AuthenticationPrincipal User currentUser, @RequestBody LinkForm form) {
//        return linkService.linkVehicleObd(currentUser, form);
//    }

    @RequestMapping(value = "linkvehicle", method = {RequestMethod.POST, RequestMethod.PUT})
    public Object linkObdVehicle(@AuthenticationPrincipal User currentUser, @RequestBody LinkForm form) {
        ObdDevice obdDevice = obdDeviceService.getObdDevice(form.getObdDeviceNo());
        if(currentUser.isAuthorized(form.getPartnerNo()) && currentUser.isAuthorized(obdDevice.getPartnerNo())) {
            return linkService.linkObdVehicle(currentUser, form);
        } else {
            return CognyStatus.ACCESS_DENYED.toEntityMap();
        }
    }

}
