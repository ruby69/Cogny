package io.dymatics.cogny.domain.persist;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import io.dymatics.cogny.domain.model.ObdDevice;

@Mapper
public interface ObdRepository {

    @Select("SELECT * FROM `obd_device` WHERE `obd_device_no` = #{value}")
    ObdDevice findByNo(Long obdDeviceNo);

    @Select("SELECT * FROM `obd_device` WHERE `obd_serial` = #{value}")
    ObdDevice findBySerial(String obdSerial);

}
