package io.dymatics.cogny.domain.persist;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import io.dymatics.cogny.domain.model.Vehicle;

@Mapper
public interface VehicleRepository {
    @Select("SELECT * FROM `vehicle` WHERE `enabled` = true AND vehicle_no = #{vehicleNo}")
    Vehicle findByNo(Long vehicleNo);
}
