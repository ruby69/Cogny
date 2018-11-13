package io.dymatics.cogny.domain.persist;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import io.dymatics.cogny.domain.model.DriveRepairLog;

@Mapper
public interface DriveRepairLogRepository {

    void insert(DriveRepairLog driveRepairLog);

    @Select(" SELECT *" +
            " FROM `drive_repair_log`" +
            " WHERE" +
            "  (" +
            "    `date_idx` BETWEEN #{p.A} AND #{p.B}" +
            "  )" +
            "  AND" +
            "  (" +
            "   (`vehicle_no` = #{p.vehicleNo} AND (`ref`='REP' OR `ref`='REPM'))" +
            "   OR (`user_no` = #{p.userNo} AND `ref`='DRIVE' )" +
            "  )" +
            " ORDER BY `date_idx` DESC, `drive_repair_log_no` ASC")
    List<DriveRepairLog> findByPage(DriveRepairLog.Page page);

    @Select("SELECT MIN(`date_idx`) FROM `drive_repair_log` WHERE `vehicle_no` = #{p.vehicleNo}")
    Integer findLastDateIdxByPage(DriveRepairLog.Page page);

}
