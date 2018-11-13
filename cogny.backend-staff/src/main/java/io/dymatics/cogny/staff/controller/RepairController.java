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
import io.dymatics.cogny.domain.model.Repair;
import io.dymatics.cogny.domain.model.User;
import io.dymatics.cogny.staff.service.RepairService;
import io.dymatics.cogny.staff.service.VehicleService;

@RestController
public class RepairController {
    @Autowired private RepairService repairService;
    
    @Autowired private VehicleService vehicleService;

    @RequestMapping(value = "repairs", method = RequestMethod.GET)
    public Object repairComponentList(@AuthenticationPrincipal User currentUser, Page page) {
        if(vehicleService.checkAuthority(currentUser,  page.getVehicleNo())) {
            return repairService.populateRepairList(page);
        } else {
            return CognyStatus.ACCESS_DENYED.toEntityMap();
        }
    }

    @RequestMapping(value = "repairs/enums", method = RequestMethod.GET)
    public Object getEnums() {
        return repairService.getRepairCategoryEnums();
    }

    @RequestMapping(value = "repairs", method = { RequestMethod.POST, RequestMethod.PUT })
    public Object save(@AuthenticationPrincipal User currentUser, @RequestBody Repair.Form form) {
        if(vehicleService.checkAuthority(currentUser, form.getVehicleNo())) {
            return repairService.saveRepair(form);
        } else {
            return CognyStatus.ACCESS_DENYED.toEntityMap();
        }
    }

    @RequestMapping(value = "repairs/{repairNo}", method = RequestMethod.DELETE)
    public Object delete(@AuthenticationPrincipal User currentUser, @PathVariable Long repairNo) {
        if(vehicleService.checkAuthority(currentUser, repairService.getRepair(repairNo).getVehicleNo())) {
            Repair repair = repairService.deleteRepair(repairNo);
            if(repair == null || repair.getEnabled() == false) {
                return CognyStatus.OK.toEntityMap();
            } else {
                return CognyStatus.UNKNOWN_ERROR.toEntityMap();
            }
        } else {
            return CognyStatus.ACCESS_DENYED.toEntityMap();
        }
    }
}
