package io.dymatics.cogny.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.dymatics.cogny.api.service.VehicleService;

@RestController
public class VehicleController {
    @Autowired private VehicleService vehicleService;

    @RequestMapping(value = "obds/{obdSerial}", method = RequestMethod.GET)
    public Object obds(@PathVariable String obdSerial) {
        return vehicleService.findObdDevice(obdSerial);
    }

    @RequestMapping(value = "vehicles/{obdDeviceNo}", method = RequestMethod.GET)
    public Object obdVehicles(@PathVariable Long obdDeviceNo) {
        return vehicleService.getObdVehicles(obdDeviceNo);
    }

    @RequestMapping(value = "vehicle/{obdDeviceNo}", method = RequestMethod.GET)
    public Object obdVehicle(@PathVariable Long obdDeviceNo) {
        return vehicleService.getObdVehicle(obdDeviceNo);
    }

}
