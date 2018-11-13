package io.dymatics.cogny.domain.persist;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import io.dymatics.cogny.domain.model.Page;
import io.dymatics.cogny.domain.model.User;
import io.dymatics.cogny.domain.model.Vehicle;

@Mapper
public interface VehicleRepository {

    @Insert("INSERT INTO `vehicle` (`model_no`,`fuel_no`,`license_no`,`model_year`,`model_month`,`partner_no`,`reg_user_no`,`memo`,`enabled`) VALUES (#{modelNo},#{fuelNo},#{licenseNo},#{modelYear},#{modelMonth},#{partnerNo},#{regUserNo},#{memo},TRUE)")
    @Options(useGeneratedKeys = true, keyProperty = "vehicleNo", keyColumn = "vehicle_no")
    void insert(Vehicle vehicle);

    Vehicle findByNo(Long vehicleNo);

    List<Vehicle> findAll();

    @Select("SELECT * FROM `vehicle` WHERE `enabled` = true")
    List<Vehicle> findAllByUse(boolean enabled);

    int countByPage(Page page);

    List<Vehicle> findByPage(Page page);

    void update(Vehicle vehicle);

    void delete(@Param("currentUser") User currentUser, @Param("vehicleNo") Long vehicleNo);


/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Select("SELECT * FROM `manufacturer` WHERE enabled = TRUE")
    List<Vehicle.Manufacturer> findManufacturerAll();

    @Select("SELECT * FROM model_group WHERE `manufacturer_no` = #{manufacturerNo} AND enabled = TRUE")
    List<Vehicle.ModelGroup> findModelGroupByManufacturer(Long manufacturerNo);

    @Select("SELECT * FROM `model` WHERE `model_group_no` = #{modelGroupNo} AND enabled = TRUE")
    List<Vehicle.Model> findModelByModelGroup(Long modelGroupNo);

    Vehicle.Model findSalesYearByNo(Long modelNo);

    @Select("SELECT * FROM `fuel` WHERE enabled = TRUE")
    List<Vehicle.Fuel> findFuelAll();

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Insert("INSERT INTO `user_vehicle` (`user_no`,`vehicle_no`) VALUES (#{userNo},#{vehicleNo})")
    void insertUserVehicle(@Param("userNo") Long userNo, @Param("vehicleNo") Long vehicleNo);
}
