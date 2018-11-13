package io.dymatics.cogny.staff.service.impl;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.dymatics.cogny.domain.model.Page;
import io.dymatics.cogny.domain.model.User;
import io.dymatics.cogny.domain.model.Vehicle;
import io.dymatics.cogny.domain.persist.ObdDeviceVehicleRepository;
import io.dymatics.cogny.domain.persist.VehicleRepository;
import io.dymatics.cogny.staff.service.VehicleService;

@Service
@Transactional(readOnly = true)
public class VehicleServiceImpl implements VehicleService {
    @Autowired private VehicleRepository vehicleRepository;
    @Autowired private ObdDeviceVehicleRepository obdDeviceVehicleRepository;

    @Override
    public Object populateVehicles(Page page) {
        page.setTotal(vehicleRepository.countByPage(page));
        page.setContents(vehicleRepository.findByPage(page));
        return page;
    }

    @Override
    public Vehicle getVehicle(Long vehicleNo) {
        return vehicleRepository.findByNo(vehicleNo);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public Object saveVehicle(Vehicle vehicle) {
        if(vehicle.getVehicleNo() != null) {
            vehicleRepository.update(vehicle);
        } else {
            vehicleRepository.insert(vehicle);
        }
        return getVehicle(vehicle.getVehicleNo());
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public Vehicle deleteVehicle(User currentUser, Long vehicleNo) {
        obdDeviceVehicleRepository.findBusyByVehicle(currentUser, vehicleNo).forEach(obdDeviceVehicle -> {
            obdDeviceVehicleRepository.update(currentUser, obdDeviceVehicle);
        });
        vehicleRepository.delete(currentUser, vehicleNo);
        return getVehicle(vehicleNo);
    }

    @Override
    public Object getFreeVehicles() {
        Set<Vehicle> busyVehicleSet = obdDeviceVehicleRepository.findBusyVehicles().stream().map(odv -> odv.getVehicle()).collect(Collectors.toSet());
        return vehicleRepository.findAll().stream().filter(vehicle -> !busyVehicleSet.contains(vehicle)).collect(Collectors.toList());
    }
    
    @Override
    public boolean checkAuthority(User currentUser, Long vehicleNo) {
        Long partnerNo = getVehicle(vehicleNo).getPartnerNo();
        if(currentUser.isAuthorized(partnerNo)) {
            return true;
        } else {
            return false;
        }
    }

}
