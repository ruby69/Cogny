package io.dymatics.cogny.domain.persist;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import io.dymatics.cogny.domain.model.RepairMsg;

@Mapper
public interface RepairMsgRepository {

    @Insert("INSERT INTO `repair_msg` (`vehicle_no`,`user_no`,`msg_type`,`call_type`) VALUES (#{vehicleNo},#{userNo},#{msgType},#{callType})")
    @Options(useGeneratedKeys = true, keyProperty = "repairMsgNo", keyColumn = "repair_msg_no")
    void insert(RepairMsg repairMsg);

    @Update("UPDATE `repair_msg` SET `repair_no` = #{repairNo}, `status` = 'COMPLETE' WHERE `vehicle_no` = #{vehicleNo} AND `repair_no` IS NULL")
    void updateRepairNo(@Param("repairNo") Long repairNo, @Param("vehicleNo") Long vehicleNo);

    @Update("UPDATE `repair_msg` SET `status` = 'INVISIBLE' WHERE `vehicle_no` = #{vehicleNo} AND `repair_no` IS NULL")
    void updateRepairMsgStatus(Long vehicleNo);

    @Select("SELECT * FROM `repair_msg` WHERE `vehicle_no` = #{value} AND `repair_no` IS NULL AND `status` = 'VISIBLE' ORDER BY `repair_msg_no` DESC LIMIT 1")
    RepairMsg findLatestOneByVehicle(Long vehicleNo);

    @Select("SELECT * FROM `repair_msg` WHERE `vehicle_no` = #{vehicleNo} AND `msg_type` != 'EMPTY' AND `repair_no` IS NULL ORDER BY `repair_msg_no` DESC LIMIT 1")
    RepairMsg findLastRepairMsgByVehicleNo(@Param("vehicleNo") Long vehicleNo);

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Insert("INSERT INTO `repair_msg_dtc_history` (`repair_msg_no`,`dtc_history_no`) VALUES (#{repairMsgNo},#{dtcHistoryNo})")
    void insertRepairMsgDtcHistory(@Param("repairMsgNo") Long repairMsgNo, @Param("dtcHistoryNo") Long dtcHistoryNo);

    @Insert("INSERT INTO `repair_msg_diagnosis` (`repair_msg_no`,`diagnosis_no`) VALUES (#{repairMsgNo},#{diagnosisNo})")
    void insertRepairMsgDiagnosis(@Param("repairMsgNo") Long repairMsgNo, @Param("diagnosisNo") Long diagnosisNo);
}
