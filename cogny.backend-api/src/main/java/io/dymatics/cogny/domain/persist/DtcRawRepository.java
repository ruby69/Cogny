package io.dymatics.cogny.domain.persist;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import io.dymatics.cogny.domain.model.DtcRaw;

@Mapper
public interface DtcRawRepository {

    @Insert("INSERT INTO `dtc_raw` (`vehicle_no`,`obd_device_no`,`drive_history_no`,`dtc_seq`,`dtc_issued_time`,`dtc_updated_time`,`dtc_code`,`dtc_state`) VALUES "
            + "(#{vehicleNo},#{obdDeviceNo},#{driveHistoryNo},#{dtcSeq},#{dtcIssuedTime},#{dtcUpdatedTime},#{dtcCode},#{dtcState})")
    void insert(DtcRaw dtcRaw);

    @Update("UPDATE `dtc_raw` SET `dtc_updated_time`=#{dtcUpdatedTime} WHERE `dtc_raw_no`=#{dtcRawNo}")
    void update(DtcRaw dtcRaw);

    @Select("SELECT * FROM `dtc_raw` WHERE `drive_history_no`=#{driveHistoryNo} AND `dtc_seq`=#{dtcSeq} AND `dtc_code`=#{dtcCode}")
    DtcRaw findByDriveHistoryAndSeq(DtcRaw dtcRaw);

}
