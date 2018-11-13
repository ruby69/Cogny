package io.dymatics.cogny.staff.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.dymatics.cogny.domain.model.CognyStatus;
import io.dymatics.cogny.domain.model.DriveHistory;
import io.dymatics.cogny.domain.model.DriveHistory.DriveHistoryMemo;
import io.dymatics.cogny.domain.model.Page;
import io.dymatics.cogny.domain.model.User;
import io.dymatics.cogny.staff.service.DriveHistoryService;
import io.dymatics.cogny.staff.service.VehicleService;

@RestController
public class DriveHistoryController {
    @Autowired
    private DriveHistoryService driveHistoryService;
    
    @Autowired
    private VehicleService vehicleService;

    @RequestMapping(value = "drivehistory/startdates/{vehicleNo}", method = RequestMethod.GET)
    public Object getStartDates(@AuthenticationPrincipal User currentUser, @PathVariable Long vehicleNo) {
        if(vehicleService.checkAuthority(currentUser, vehicleNo)) {
            return driveHistoryService.getStartDates(vehicleNo);
        } else {
            return CognyStatus.ACCESS_DENYED.toEntityMap();
        }
        
    }

    @RequestMapping(value = "drivehistory/indexes", method = RequestMethod.GET)
    public Object getDriveHistoryIndexes(@AuthenticationPrincipal User currentUser, DriveHistory driveHistory) {
        if(vehicleService.checkAuthority(currentUser, driveHistory.getVehicleNo())) {
            return driveHistoryService.getDriveHistoryIndexes(driveHistory);
        } else {
            return CognyStatus.ACCESS_DENYED.toEntityMap();
        }
    }

    @RequestMapping(value = "drivehistory/detail", method = RequestMethod.GET)
    public Object getDriveHistoryByNo(@AuthenticationPrincipal User currentUser, DriveHistory driveHistory) {
        if(vehicleService.checkAuthority(currentUser, driveHistory.getVehicleNo())) {
            return driveHistoryService.getDriveHisotyByNo(driveHistory.getDriveHistoryNo());
        } else {
            return CognyStatus.ACCESS_DENYED.toEntityMap();
        }
    }
    
    @RequestMapping(value = "drivehistory/dtcs", method = RequestMethod.GET)
    public Object getDrieHistoryWithDtcRaw (@AuthenticationPrincipal User currentUser, Page page) {
        if(page.getVehicleNo() != null
                && !currentUser.isAuthorized(vehicleService.getVehicle(page.getVehicleNo()).getPartnerNo())) {
            return CognyStatus.ACCESS_DENYED.toEntityMap();
        } else if(page.getPartnerNo() != null
                && !currentUser.isAuthorized(page.getPartnerNo())) {
            return CognyStatus.ACCESS_DENYED.toEntityMap();
        } else {
            page.setCurrentUser(currentUser);
            return driveHistoryService.populateDriveHistory(page);
        }
    }
    @RequestMapping(value = "drivehistory/memo", method = { RequestMethod.POST, RequestMethod.PUT })
    public Object save(@AuthenticationPrincipal User currentUser, @RequestBody DriveHistoryMemo driveHistoryMemo) {
        return driveHistoryService.saveMemo(driveHistoryMemo);
    }
}
