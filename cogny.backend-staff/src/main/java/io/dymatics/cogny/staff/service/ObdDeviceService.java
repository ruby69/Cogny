package io.dymatics.cogny.staff.service;

import java.util.List;

import io.dymatics.cogny.domain.model.ObdDevice;
import io.dymatics.cogny.domain.model.Page;
import io.dymatics.cogny.domain.model.User;

public interface ObdDeviceService {

    Object populateObdDevices(Page page);

    ObdDevice getObdDevice(Long obdDeviceNo);

    Object getObdDeviceList(List<ObdDevice> obdDeviceList);

    Object saveObdDevice(ObdDevice ovdDevice);

    Object saveObdDeviceList(ObdDevice.Form form);

    ObdDevice deleteObdDevice(User currentUser, Long obdDeviceNo);

    Object getFreeObdDevices();

}
