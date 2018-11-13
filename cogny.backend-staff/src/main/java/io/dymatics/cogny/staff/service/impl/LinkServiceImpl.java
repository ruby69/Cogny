package io.dymatics.cogny.staff.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.dymatics.cogny.domain.model.LinkForm;
import io.dymatics.cogny.domain.model.ObdDevice;
import io.dymatics.cogny.domain.model.ObdDeviceVehicle;
import io.dymatics.cogny.domain.model.User;
import io.dymatics.cogny.domain.persist.ObdDeviceRepository;
import io.dymatics.cogny.domain.persist.ObdDeviceVehicleRepository;
import io.dymatics.cogny.staff.service.LinkService;
import io.dymatics.cogny.staff.service.ObdDeviceService;
import io.dymatics.cogny.staff.service.VehicleService;

@Service
@Transactional(readOnly = true)
public class LinkServiceImpl implements LinkService {
    @Autowired
    private ObdDeviceRepository obdDeviceRepository;
    @Autowired
    private ObdDeviceVehicleRepository obdDeviceVehicleRepository;

    @Autowired
    private VehicleService vehicleService;
    @Autowired
    private ObdDeviceService obdDeviceService;

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public Object linkVehicleObd(User currentUser, LinkForm form) {
        if (form.isSetup()) {
            setupVehicleObd(form.getVehicleNo(), form.getObdDeviceNo());
            return vehicleService.getVehicle(form.getVehicleNo());
        } else {
            Long obdDeviceVehicleNo = form.getObdDeviceVehicleNo();
            ObdDeviceVehicle find = obdDeviceVehicleRepository.findByNo(obdDeviceVehicleNo);

            if (form.isModify()) {
                releaseVehicleObd(currentUser, find);
                setupVehicleObd(find.getVehicleNo(), form.getObdDeviceNo());

            } else if (form.isRelease()) {
                releaseVehicleObd(currentUser, find);
            }

            return vehicleService.getVehicle(find.getVehicleNo());
        }
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public Object linkObdVehicle(User currentUser, LinkForm form) {
        // OBD Device Serial 또는 partnerNo가 변경된 경우 DB를 수정한다.
        ObdDevice oldObdDevice = obdDeviceRepository.findByNo(form.getObdDeviceNo());
        if (!form.getObdSerial().equals(oldObdDevice.getObdSerial()) 
                || form.getPartnerNo() != oldObdDevice.getPartnerNo()) {
            obdDeviceService.saveObdDevice(form.getObdDevice());
        }

        // OBD 단말기를 신규 설치하는 경우
        if (form.isSetup()) {
            setupVehicleObd(form.getVehicleNo(), form.getObdDeviceNo());
            return obdDeviceService.getObdDevice(form.getObdDeviceNo());
        }

        Long obdDeviceVehicleNo = form.getObdDeviceVehicleNo();
        ObdDeviceVehicle find = obdDeviceVehicleRepository.findByNo(obdDeviceVehicleNo);

        // OBD 단말기를 다른 차량에 설치하는 경우
        if (form.getVehicleNo() != null && find != null && form.getVehicleNo() != find.getVehicleNo()) {
            releaseVehicleObd(currentUser, find);
            setupVehicleObd(form.getVehicleNo(), find.getObdDeviceNo());

            // OBD 단말기를 차량에서 제거하는 경우
        } else if (form.getVehicleNo() == null && find != null && find.getVehicleNo() != null) {
            releaseVehicleObd(currentUser, find);
        }
        return obdDeviceService.getObdDevice(form.getObdDeviceNo());

    }

    private void setupVehicleObd(Long vehicleNo, Long obdDeviceNo) {
        ObdDevice obdDevice = obdDeviceRepository.findByNo(obdDeviceNo);
        if (obdDevice.isFree()) {
            obdDeviceVehicleRepository.insert(new ObdDeviceVehicle(obdDeviceNo, vehicleNo, new Date()));
        }
    }

    private void releaseVehicleObd(User currentUser, ObdDeviceVehicle obdDeviceVehicle) {
        ObdDevice obdDevice = obdDeviceVehicle.getObdDevice();
        if (obdDevice.isBusy()) {
            obdDeviceVehicleRepository.update(currentUser, obdDeviceVehicle);
        }
    }
}
