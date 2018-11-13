package io.dymatics.cogny.api.service;

public interface VehicleService {

//    Object getUserVehicles(Long userNo);
//
//    Object getUserVehicle(Long userVehicleNo);
//
//    Object saveUserVehicle(Vehicle.UserVehicleForm form, User user);
//
//    Object deleteUserVehicle(Long userVehicleNo, User user);

    Object getObdVehicles(Long obdDeviceNo);

    Object findObdDevice(String obdSerial);

    Object getObdVehicle(Long obdDeviceNo);
}
