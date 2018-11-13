package io.dymatics.cogny.domain.persist;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

import io.dymatics.cogny.domain.model.Repair;

@Mapper
public interface RepairRepository {

    @Insert("INSERT INTO `repair` (`vehicle_no`,`user_no`,`odometer`,`repair_date`) VALUES (#{vehicleNo},#{userNo},#{odometer},CURDATE())")
    @Options(useGeneratedKeys = true, keyProperty = "repairNo", keyColumn = "repair_no")
    void insert(Repair repair);

}
