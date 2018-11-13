package io.dymatics.cogny.domain.persist;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import io.dymatics.cogny.domain.model.SensingRaw;

@Mapper
public interface SensingRawRepository {

    @Insert("INSERT INTO `sensing_raw` (" +
            "  `vehicle_no`,`obd_device_no`,`drive_history_no`,`sensing_seq`,`sensing_time`,`battery_volt`,`ignition_volt`," +
            "  `engine_rpm`,`map_pa`,`coolant_temp`,`oxy_volt_s2`,`oxy_volt_s1`,`oxy_volt_s1_lin`,`vehicle_spd`,`injection_time_c1`," +
            "  `injection_time_c2`,`injection_time_c3`,`injection_time_c4`,`ignition_switch`,`brake_lamp_switch`," +
            "  `af_ratio_ctrl_activation`,`ac_compressor_on`,`cmp_ckp_sync`,`cooling_fan_relay_hs`,`knock_sensing`," +
            "  `battery_sensor_current`,`battery_sensor_volt`,`alternator_target_volt`,`timer_ig_on`,`torque_cvt_tub_spd`," +
            "  `ac_pa_volt`,`required_af_ratio`,`actual_af_ratio`,`ig_timing_c1`,`ig_failure_cnt_c1`,`ig_failure_cnt_c2`," +
            "  `ig_failure_cnt_c3`,`ig_failure_cnt_c4`,`ig_failure_cnt_catalyst_c1`,`ig_failure_cnt_catalyst_c2`," +
            "  `ig_failure_cnt_catalyst_c3`,`ig_failure_cnt_catalyst_c4`,`tps_volt1`,`tps_volt2`,`accel_pedal_volt1`," +
            "  `accel_pedal_volt2`,`etc_motor_duty`,`lpg_fuel_rail_pa`,`lpg_fuel_rail_pa_volt`,`fuel_pump_ctrl`," +
            "  `fuel_pump_fault`,`butane_ratio`,`tire_pressure1`,`tire_pressure2`,`tire_pressure3`,`tire_pressure4`," +
            "  `manifold_air_temp`,`engine_oil_temp`,`odometer`" +
            ") VALUES (" +
            "  #{vehicleNo},#{obdDeviceNo},#{driveHistoryNo},#{sensingSeq},#{sensingTime},#{batteryVolt},#{ignitionVolt},#{engineRpm}," +
            "  #{mapPa},#{coolantTemp},#{oxyVoltS2},#{oxyVoltS1},#{oxyVoltS1Lin},#{vehicleSpd},#{injectionTimeC1},#{injectionTimeC2}," +
            "  #{injectionTimeC3},#{injectionTimeC4},#{ignitionSwitch},#{brakeLampSwitch},#{afRatioCtrlActivation}," +
            "  #{acCompressorOn},#{cmpCkpSync},#{coolingFanRelayHs},#{knockSensing},#{batterySensorCurrent}," +
            "  #{batterySensorVolt},#{alternatorTargetVolt},#{timerIgOn},#{torqueCvtTubSpd },#{acPaVolt},#{requiredAfRatio}," +
            "  #{actualAfRatio},#{igTimingC1},#{igFailureCntC1},#{igFailureCntC2},#{igFailureCntC3},#{igFailureCntC4}," +
            "  #{igFailureCntCatalystC1},#{igFailureCntCatalystC2},#{igFailureCntCatalystC3},#{igFailureCntCatalystC4}," +
            "  #{tpsVolt1},#{tpsVolt2},#{accelPedalVolt1},#{accelPedalVolt2},#{etcMotorDuty},#{lpgFuelRailPa}," +
            "  #{lpgFuelRailPaVolt},#{fuelPumpCtrl},#{fuelPumpFault},#{butaneRatio},#{tirePressure1},#{tirePressure2}," +
            "  #{tirePressure3},#{tirePressure4},#{manifoldAirTemp},#{engineOilTemp},#{odometer}" +
            ")")
//    @Options(useGeneratedKeys = true, keyProperty = "sensingRawNo", keyColumn = "sensing_raw_no")
    void insert(SensingRaw sensingRaw);

    @Select("SELECT * FROM `sensing_raw` WHERE `drive_history_no` = #{driveHistoryNo} ORDER BY `sensing_raw_no` DESC LIMIT 1")
    SensingRaw findLastOneByDriveHistory(Long driveHistoryNo);

    @Select("SELECT count(1) FROM `sensing_raw` WHERE `drive_history_no` = #{driveHistoryNo} AND `sensing_seq` = #{sensingSeq}")
    long countOfDriveHistoryAndSensingSeq(SensingRaw sensingRaw);

}
