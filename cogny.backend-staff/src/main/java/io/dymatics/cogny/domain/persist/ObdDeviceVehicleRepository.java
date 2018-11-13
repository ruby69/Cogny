package io.dymatics.cogny.domain.persist;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import io.dymatics.cogny.domain.model.ObdDeviceVehicle;
import io.dymatics.cogny.domain.model.User;

@Mapper
public interface ObdDeviceVehicleRepository {

    @Insert("INSERT INTO `obd_device_vehicle` (`obd_device_no`,`vehicle_no`,`install_date`,`uninstall_date`) VALUES (#{obdDeviceNo},#{vehicleNo},#{installDate},#{uninstallDate})")
    @Options(useGeneratedKeys = true, keyProperty = "obdDeviceVehicleNo", keyColumn = "obd_device_vehicle_no")
    void insert(ObdDeviceVehicle obdDeviceVehicle);

    @Select("SELECT * FROM `obd_device_vehicle`")
    @ResultMap("obdDeviceVehicleResultMap")
    List<ObdDeviceVehicle> findAll();

    @Select("SELECT * FROM `obd_device_vehicle` WHERE `uninstall_date` is NULL")
    @ResultMap("obdDeviceVehicleResultMap")
    List<ObdDeviceVehicle> findBusyVehicles();

    void update(@Param("currentUser") User currentUser, @Param("obdDeviceVehicle") ObdDeviceVehicle obdDeviceVehicle);

    List<ObdDeviceVehicle> findBusyByObdDevice(@Param("currentUser") User currentUser, @Param("obdDeviceNo") Long obdDeviceNo);

    List<ObdDeviceVehicle> findBusyByVehicle(@Param("currentUser") User currentUser,  @Param("vehicleNo") Long vehicleNo);

    @Select("SELECT * FROM `obd_device_vehicle` WHERE `obd_device_vehicle_no` = #{obdDeviceVehicleNo}")
    @ResultMap("obdDeviceVehicleResultMap")
    ObdDeviceVehicle findByNo(Long obdDeviceVehicleNo);

    @Select("SELECT * FROM `obd_device_vehicle` WHERE `uninstall_date` is NULL LIMIT 1")
    @ResultMap("obdDeviceVehicleResultMap")
    ObdDeviceVehicle findBusyVehicle();
}
