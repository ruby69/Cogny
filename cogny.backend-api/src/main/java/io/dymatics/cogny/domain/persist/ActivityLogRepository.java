package io.dymatics.cogny.domain.persist;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import io.dymatics.cogny.domain.model.ActivityLog;

@Mapper
public interface ActivityLogRepository {

    @Insert("INSERT INTO `activity_log` (" +
            "  `user_no`,`activity_seq`,`mobile_device_no`,`vehicle_no`,`obd_device_no`,`drive_history_no`,`category`,`activity`,`activity_time`" +
            ") VALUES (" +
            "  #{userNo},#{activitySeq},#{mobileDeviceNo},#{vehicleNo},#{obdDeviceNo},#{driveHistoryNo},#{category},#{activity},#{activityTime}" +
            ")")
    void insert(ActivityLog activityLog);

    @Select("SELECT count(1) FROM `activity_log` WHERE `user_no` = #{userNo} AND `activity_seq` = #{activitySeq}")
    long countOfUserNoAndActivitySeq(ActivityLog activityLog);
}
