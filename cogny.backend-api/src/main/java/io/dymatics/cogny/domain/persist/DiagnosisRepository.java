package io.dymatics.cogny.domain.persist;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import io.dymatics.cogny.domain.model.Diagnosis;

@Mapper
public interface DiagnosisRepository {

    @Select("SELECT" +
            "  di.`diagnosis_cate_no`," +
            "  di.`name`," +
            "  di.`service_msg`," +
            "  d.`diagnosis_result`," +
            "  d.`diagnosis_msg` " +
            "FROM" +
            "  `diagnosis` d" +
            "  INNER JOIN (" +
            "    SELECT MAX(`diagnosis_no`) AS `diagnosis_no`" +
            "    FROM `diagnosis` d" +
            "    WHERE d.`repair_no` IS NULL AND d.`vehicle_no` = #{value}" +
            "    GROUP BY d.`diagnosis_item_no` " +
            "  ) dl ON dl.`diagnosis_no` = d.`diagnosis_no`" +
            "  INNER JOIN `diagnosis_item` di ON d.`diagnosis_item_no` = di.`diagnosis_item_no`")
    List<Diagnosis.Summary> findLastSummariesByVehicleNo(Long vehicleNo);

    @Update("UPDATE `diagnosis` SET `repair_no` = #{repairNo}, `upd_date` = NOW() WHERE `vehicle_no` = #{vehicleNo} AND `repair_no` IS NULL AND (`diagnosis_result`='CAUTION' OR `diagnosis_result`='FATAL')")
    void updateRepairNo(@Param("repairNo") Long repairNo, @Param("vehicleNo") Long vehicleNo);

    @Select("SELECT * FROM `diagnosis` WHERE `vehicle_no` = #{value} AND `repair_no` IS NULL AND (`diagnosis_result`='CAUTION' OR `diagnosis_result`='FATAL')")
    List<Diagnosis> findRepairNoIsNull(Long vehicleNo);


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Select("SELECT * FROM `diagnosis_cate`")
    List<Diagnosis.Category> findDiagnosisCategory();
}
