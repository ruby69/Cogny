package io.dymatics.cogny.staff.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.dymatics.cogny.domain.model.ObdDevice;
import io.dymatics.cogny.domain.model.Page;
import io.dymatics.cogny.domain.model.User;
import io.dymatics.cogny.domain.persist.ObdDeviceRepository;
import io.dymatics.cogny.domain.persist.ObdDeviceVehicleRepository;
import io.dymatics.cogny.staff.service.ObdDeviceService;

@Service
@Transactional(readOnly = true)
//@Slf4j
public class ObdDeviceServiceImpl implements ObdDeviceService {
    @Autowired
    private ObdDeviceRepository obdDeviceRepository;
    @Autowired
    private ObdDeviceVehicleRepository obdDeviceVehicleRepository;

    @Override
    public Object populateObdDevices(Page page) {
        page.setTotal(obdDeviceRepository.countByPage(page));
        page.setContents(obdDeviceRepository.findByPage(page));
        return page;
    }

    @Override
    public ObdDevice getObdDevice(Long obdDeviceNo) {
        return obdDeviceRepository.findByNo(obdDeviceNo);
    }

    @Override
    public Object getObdDeviceList(List<ObdDevice> obdDeviceList) {
        return obdDeviceRepository.findByList(obdDeviceList);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public Object saveObdDevice(ObdDevice obdDevice) {
        if (obdDevice.getObdDeviceNo() != null) {
            obdDeviceRepository.update(obdDevice);
//        } else {
//            obdDeviceRepository.insert(obdDevice);
        } else {
            return null;
        }
        return getObdDevice(obdDevice.getObdDeviceNo());
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public Object saveObdDeviceList(ObdDevice.Form form) {
        obdDeviceRepository.insertList(form);
        return form;
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public ObdDevice deleteObdDevice(User currentUser, Long obdDeviceNo) {
        obdDeviceVehicleRepository.findBusyByObdDevice(currentUser, obdDeviceNo).forEach(obdDeviceVehicle -> {
            obdDeviceVehicleRepository.update(currentUser, obdDeviceVehicle);
        });
        obdDeviceRepository.delete(currentUser, obdDeviceNo);
        return getObdDevice(obdDeviceNo);
    }

    @Override
    public Object getFreeObdDevices() {
        return obdDeviceRepository.findAllFree();
    }

}
