package io.dymatics.cogny.domain.persist;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import io.dymatics.cogny.domain.model.MobileDevice;

@Mapper
public interface MobileDeviceRepository {

    @Insert("INSERT INTO `mobile_device` (`uuid`,`os`) VALUES (#{uuid},#{os})")
    @Options(useGeneratedKeys = true, keyProperty = "mobileDeviceNo", keyColumn = "mobile_device_no")
    void insert(MobileDevice mobileDevice);

    @Select("SELECT * FROM `mobile_device` WHERE `uuid` = #{value }")
    MobileDevice findByUuid(String uuid);

}
