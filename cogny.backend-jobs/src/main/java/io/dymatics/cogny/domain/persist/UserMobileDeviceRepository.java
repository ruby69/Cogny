package io.dymatics.cogny.domain.persist;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import io.dymatics.cogny.domain.model.MobileDevice;

@Mapper
public interface UserMobileDeviceRepository {

    MobileDevice.UserMobileDevice findByNo(Long userMobileDeviceNo);
    
    List<MobileDevice.UserMobileDevice> findByVehicleNo(Long vehicleNo);
}
