package io.dymatics.cogny.staff.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.dymatics.cogny.domain.persist.VehicleRepository;
import io.dymatics.cogny.staff.service.MiscService;

@Service
@Transactional(readOnly = true)
public class MiscServiceImpl implements MiscService {
    @Autowired private VehicleRepository vehicleRepository;

    @Override
    public Object getFuels() {
        return vehicleRepository.findFuelAll();
    }

    @Override
    public Object getManufacturers() {
        return vehicleRepository.findManufacturerAll();
    }
    
    @Override
    public Object getModelGouprs(Long manufacturerNo) {
        return vehicleRepository.findModelGroupByManufacturer(manufacturerNo);
    }
    
    @Override
    public Object getModels(Long modelGroupNo) {
        return vehicleRepository.findModelByModelGroup(modelGroupNo);
    }
    
    @Override
    public Object getModelSalesYear(Long modelNo) {
        return vehicleRepository.findSalesYearByNo(modelNo);
    }

}
