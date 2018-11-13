package io.dymatics.cogny.domain.persist;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import io.dymatics.cogny.domain.model.DtcHistory;

@Mapper
public interface DtcHistoryRepository {

    @Select("SELECT * FROM `dtc_history` WHERE `vehicle_no` = #{value} AND `repair_no` IS NULL")
    List<DtcHistory> findRepairNoIsNull(Long vehicleNo);

    @Update("UPDATE `dtc_history` SET `repair_no`=#{repairNo} WHERE `vehicle_no` = #{vehicleNo} AND `repair_no` IS NULL")
    void updateRepairNo(@Param("repairNo") Long repairNo, @Param("vehicleNo") Long vehicleNo);

}
