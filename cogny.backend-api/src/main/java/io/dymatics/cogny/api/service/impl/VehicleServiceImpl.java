package io.dymatics.cogny.api.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.dymatics.cogny.api.service.VehicleService;
import io.dymatics.cogny.domain.persist.ObdRepository;
import io.dymatics.cogny.domain.persist.VehicleRepository;

@Service
@Transactional(readOnly = true)
public class VehicleServiceImpl implements VehicleService {
    @Autowired private VehicleRepository vehicleRepository;
    @Autowired private ObdRepository obdRepository;

    @Override
    public Object findObdDevice(String obdSerial) {
        return obdRepository.findBySerial(obdSerial);
    }

    @Override
    public Object getObdVehicles(Long obdDeviceNo) {
        return vehicleRepository.findByObd(obdDeviceNo);
    }

    @Override
    public Object getObdVehicle(Long obdDeviceNo) {
        return vehicleRepository.findOneByObd(obdDeviceNo);
    }
}
