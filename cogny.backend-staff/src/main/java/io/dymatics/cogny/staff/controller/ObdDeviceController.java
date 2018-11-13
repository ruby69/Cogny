package io.dymatics.cogny.staff.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.dymatics.cogny.domain.model.CognyStatus;
import io.dymatics.cogny.domain.model.ObdDevice;
import io.dymatics.cogny.domain.model.Page;
import io.dymatics.cogny.domain.model.User;
import io.dymatics.cogny.staff.service.ObdDeviceService;

@RestController
public class ObdDeviceController {
    @Autowired private ObdDeviceService obdDeviceService;

    @RequestMapping(value = "obds", method = RequestMethod.GET)
    public Object obds(@AuthenticationPrincipal User currentUser, Page page) {
        if(currentUser.isAuthorized(page.getPartnerNo())) {
            page.setCurrentUser(currentUser);
            page.setAuthorizedPartnerNo(currentUser.getAuthorizedPartnerNo(page.getPartnerNo()));
            return obdDeviceService.populateObdDevices(page);
        } else {
            return CognyStatus.UNAUTHORIZED_PARAMETERS.toEntityMap();
        }
    }

    @RequestMapping(value = "obds/{obdDeviceNo}", method = RequestMethod.GET)
    public Object obds(@AuthenticationPrincipal User currentUser, @PathVariable Long obdDeviceNo) {
        ObdDevice obdDevice = obdDeviceService.getObdDevice(obdDeviceNo);
        if(currentUser.isAuthorized(obdDevice.getPartnerNo())) {
            return obdDevice;
        } else {
            return CognyStatus.ACCESS_DENYED.toEntityMap();
        }
    }
    
    @RequestMapping(value = "obds/isdup", method = RequestMethod.POST)
    public Object obds(@RequestBody ObdDevice.Form form) {
        return obdDeviceService.getObdDeviceList(form.getObdDeviceList());
    }

    @RequestMapping(value = "obds", method = {RequestMethod.POST, RequestMethod.PUT})
    public Object save(@AuthenticationPrincipal User currentUser, @RequestBody ObdDevice.Form form) {
        ObdDevice obdDevice = form.getObdDevice();
        if(currentUser.isAuthorized(form.getPartnerNo())) {
            obdDevice.setRegUserNo(currentUser.getUserNo());
        } else {
            return CognyStatus.UNAUTHORIZED_PARAMETERS.toEntityMap();
        }
        return obdDeviceService.saveObdDevice(obdDevice);
    }

    @RequestMapping(value = "obds/list", method = {RequestMethod.POST, RequestMethod.PUT})
    public Object saveList(@AuthenticationPrincipal User currentUser, @RequestBody ObdDevice.Form form) {
        if(currentUser.isAuthorized(form.getPartnerNo())){
            form.setCurrentUser(currentUser);
        } else {
            return CognyStatus.UNAUTHORIZED_PARAMETERS.toEntityMap();
        }
        return obdDeviceService.saveObdDeviceList(form);
    }

    @RequestMapping(value = "obds/{obdDeviceNo}", method = RequestMethod.DELETE)
    public Object delete(@AuthenticationPrincipal User currentUser, @PathVariable Long obdDeviceNo) {
        if(!currentUser.isAuthorized(obdDeviceService.getObdDevice(obdDeviceNo).getPartnerNo())) return CognyStatus.UNAUTHORIZED_PARAMETERS.toEntityMap();

        ObdDevice obdDevice = obdDeviceService.deleteObdDevice(currentUser, obdDeviceNo);
        
        if(obdDevice.getEnabled() == false) {
            return CognyStatus.OK.toEntityMap();
        } else {
            return CognyStatus.UNKNOWN_ERROR.toEntityMap();
        }
    }

    @RequestMapping(value = "obds/free", method = RequestMethod.GET)
    public Object obdsFree() {
        return obdDeviceService.getFreeObdDevices();
    }

}
