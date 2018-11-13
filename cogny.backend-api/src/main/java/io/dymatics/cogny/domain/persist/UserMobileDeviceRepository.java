package io.dymatics.cogny.domain.persist;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import io.dymatics.cogny.domain.model.MobileDevice;

@Mapper
public interface UserMobileDeviceRepository {

    @Insert("INSERT INTO `user_mobile_device` (`user_no`,`mobile_device_no`,`mobile_number`,`fcm_token`,`push_agree`,`push_agree_date`) VALUES (#{userNo},#{mobileDeviceNo},#{mobileNumber},#{fcmToken},#{pushAgree},#{pushAgreeDate})")
    @Options(useGeneratedKeys = true, keyProperty = "userMobileDeviceNo", keyColumn = "user_mobile_device_no")
    void insert(MobileDevice.UserMobileDevice userMobileDevice);

    @Update("UPDATE `user_mobile_device` SET `mobile_number`=#{mobileNumber},`fcm_token`=#{fcmToken},`push_agree`=#{pushAgree},`push_agree_date`=#{pushAgreeDate} WHERE `user_mobile_device_no`=#{userMobileDeviceNo}")
    void update(MobileDevice.UserMobileDevice userMobileDevice);

    @Select("SELECT * FROM `user_mobile_device` WHERE `user_no` = #{userNo} AND `mobile_device_no` = #{mobileDeviceNo}")
    MobileDevice.UserMobileDevice findByUserAndMobileDevice(MobileDevice.UserMobileDevice userMobileDevice);

    @Select("SELECT * FROM `user_mobile_device` WHERE `user_mobile_device_no` = #{value}")
    MobileDevice.UserMobileDevice findByNo(Long userMobileDeviceNo);

    @Select("SELECT umd.*, u.`partner_no` FROM `user` u INNER JOIN user_mobile_device umd ON u.`user_no` = umd.`user_no` WHERE u.`partner_no`=#{value} AND u.`role`='PARTNER_MECHANIC'")
    List<MobileDevice.UserMobileDevice> findUserMobileDeviceAsPartnerMechanic(Long partnerNo);

}
