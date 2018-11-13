package io.dymatics.cogny.domain.persist;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import io.dymatics.cogny.domain.model.Vehicle;

@Mapper
public interface VehicleRepository {

    List<Vehicle> findByObd(Long obdDeviceNo);

    Vehicle findOneByObd(Long obdDeviceNo);

}
