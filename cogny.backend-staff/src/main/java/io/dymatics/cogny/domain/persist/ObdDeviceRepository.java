package io.dymatics.cogny.domain.persist;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import io.dymatics.cogny.domain.model.ObdDevice;
import io.dymatics.cogny.domain.model.Page;
import io.dymatics.cogny.domain.model.User;

@Mapper
public interface ObdDeviceRepository {

//    @Insert("INSERT INTO `obd_device` (`obd_serial`,`enabled`, `partner_no`, `reg_user_no`) VALUES (#{obdSerial}, TRUE, #{partnerNo}, #{regUserNo})")
//    @Options(useGeneratedKeys = true, keyProperty = "obdDeviceNo", keyColumn = "obd_device_no")
//    void insert(ObdDevice obdDevice);

    void insertList(ObdDevice.Form form);

    ObdDevice findByNo(Long obdDeviceNo);

    List<ObdDevice> findAllFree();

    int countByPage(Page page);

    List<ObdDevice> findByPage(Page page);

    List<ObdDevice> findByList(@Param("obdDeviceList") List<ObdDevice> obdDeviceList);

    void update(ObdDevice obdDevice);

    void delete(@Param("currentUser") User currentUser, @Param("obdDeviceNo") Long obdDeviceNo);

}
