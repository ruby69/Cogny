package io.dymatics.cogny.domain.persist;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import io.dymatics.cogny.domain.model.DriveHistory;

@Mapper
public interface DriveHistoryRepository {

    @Insert("INSERT INTO `drive_history` (`vehicle_no`,`obd_device_no`,`user_no`,`user_mobile_device_no`,`start_date`,`start_time`,`start_mileage`) VALUES (#{vehicleNo},#{obdDeviceNo},#{userNo},#{userMobileDeviceNo},#{startDate},#{startTime},#{startMileage})")
    @Options(useGeneratedKeys = true, keyProperty = "driveHistoryNo", keyColumn = "drive_history_no")
    void insert(DriveHistory driveHistory);

    @Select("SELECT * FROM `drive_history` WHERE `drive_history_no` = #{value}")
    DriveHistory findByNo(Long driveHistoryNo);

    @Update("UPDATE `drive_history` SET `end_time` = #{endTime}, `end_mileage` = #{endMileage}, `drive_distance` = #{driveDistance}, `upd_date` = NOW() WHERE `drive_history_no` = #{driveHistoryNo}")
    void update(DriveHistory driveHistory);

    @Select("SELECT * FROM `drive_history` WHERE `vehicle_no` = #{vehicleNo} AND `obd_device_no` = #{obdDeviceNo} AND `user_no` = #{userNo} AND `end_time` IS NULL ORDER BY `drive_history_no` DESC LIMIT 1")
    DriveHistory findLastOne(@Param("vehicleNo") Long vehicleNo, @Param("obdDeviceNo") Long obdDeviceNo, @Param("userNo") Long userNo);

    @Select("SELECT * FROM `drive_history` WHERE `vehicle_no` = #{vehicleNo} AND `obd_device_no` = #{obdDeviceNo} AND `end_time` IS NOT NULL ORDER BY `drive_history_no` DESC LIMIT 1")
    DriveHistory findFinLastOne(@Param("vehicleNo") Long vehicleNo, @Param("obdDeviceNo") Long obdDeviceNo);

}
