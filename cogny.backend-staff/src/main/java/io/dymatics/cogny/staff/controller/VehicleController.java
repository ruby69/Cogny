package io.dymatics.cogny.staff.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.dymatics.cogny.domain.model.CognyStatus;
import io.dymatics.cogny.domain.model.Page;
import io.dymatics.cogny.domain.model.User;
import io.dymatics.cogny.domain.model.Vehicle;
import io.dymatics.cogny.staff.service.VehicleService;

@RestController
//@Slf4j
public class VehicleController {
    @Autowired private VehicleService vehicleService;

    @RequestMapping(value = "vehicles", method = RequestMethod.GET)
    public Object vehicles(@AuthenticationPrincipal User currentUser, Page page) {
        if(currentUser.isAuthorized(page.getPartnerNo())) {
            page.setCurrentUser(currentUser);
            page.setAuthorizedPartnerNo(currentUser.getAuthorizedPartnerNo(page.getPartnerNo()));
            return vehicleService.populateVehicles(page);
        } else {
            return CognyStatus.UNAUTHORIZED_PARAMETERS.toEntityMap();
        }
    }

    @RequestMapping(value = "vehicles/{vehicleNo}", method = RequestMethod.GET)
    public Object vehicle(@AuthenticationPrincipal User currentUser, @PathVariable Long vehicleNo) {
        if(vehicleService.checkAuthority(currentUser,  vehicleNo)) {
            return vehicleService.getVehicle(vehicleNo);
        } else {
            return CognyStatus.ACCESS_DENYED.toEntityMap();
        }
        
    }

    @RequestMapping(value = "vehicles", method = {RequestMethod.POST, RequestMethod.PUT})
    public Object save(@AuthenticationPrincipal User currentUser, @RequestBody Vehicle.Form form) {
        Vehicle vehicle = form.getVehicle();
        if(currentUser.isAuthorized(form.getPartnerNo())) {
            vehicle.setRegUserNo(currentUser.getUserNo());
            return vehicleService.saveVehicle(vehicle);
        } else {
            return CognyStatus.UNAUTHORIZED_PARAMETERS.toEntityMap();
        }
    }

    @RequestMapping(value = "vehicles/{vehicleNo}", method = RequestMethod.DELETE)
    public Object delete(@AuthenticationPrincipal User currentUser, @PathVariable Long vehicleNo) {
        if(! currentUser.isAuthorized(vehicleService.getVehicle(vehicleNo).getPartnerNo())) return CognyStatus.UNAUTHORIZED_PARAMETERS.toEntityMap();

        Vehicle vehicle = vehicleService.deleteVehicle(currentUser, vehicleNo);
        if(vehicle.getEnabled() == false) {
            return CognyStatus.OK.toEntityMap();
        } else {
            return CognyStatus.UNKNOWN_ERROR.toEntityMap();
        }
    }

    @RequestMapping(value = "vehicles/free", method = RequestMethod.GET)
    public Object vehiclesFree() {
        return vehicleService.getFreeVehicles();
    }

}
