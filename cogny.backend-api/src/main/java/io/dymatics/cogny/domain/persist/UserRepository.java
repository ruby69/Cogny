package io.dymatics.cogny.domain.persist;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import io.dymatics.cogny.domain.model.User;

@Mapper
public interface UserRepository {

    @Insert("INSERT INTO `user` (`partner_no`,`uuid`,`sign_provider`,`email`,`name`,`hp_no`,`role`,`user_status`) VALUES (#{partnerNo},#{uuid},#{signProvider},#{email},#{name},#{hpNo},#{role},'MEMBER')")
    @Options(useGeneratedKeys = true, keyProperty = "userNo", keyColumn = "user_no")
    void insert(User user);

    @Select("SELECT" +
            "  u.*," +
            "  p.`company_name` AS `partner_name` " +
            "FROM" +
            "  `user` u" +
            "  INNER JOIN `partner` p ON u.`partner_no` = p.`partner_no` " +
            "WHERE " +
            "  u.`user_no` = #{value }")
    User findByNo(Long userNo);

    @Select("SELECT" +
            "  u.*," +
            "  p.`company_name` AS `partner_name` " +
            "FROM" +
            "  `user` u" +
            "  INNER JOIN `partner` p ON u.`partner_no` = p.`partner_no` " +
            "WHERE " +
            "  u.`uuid` = #{value }")
    User findByUuid(String uuid);

    @Update("UPDATE `user` SET `uuid`=#{uuid}, `sign_provider` = #{signProvider}, `email` = #{email}, `hp_no` = #{hpNo}, `upd_date` = NOW() WHERE `user_no` = #{userNo}")
    void update(User user);

    @Update("UPDATE `user` SET `name`=#{name} WHERE `user_no` = #{userNo}")
    void updateUserName(@Param("name") String name, @Param("userNo") Long userNo);

    @Update("UPDATE `user` SET `uuid` = `user_no`, `user_status` = 'QUIT', `del_date` = NOW() WHERE `user_no` = #{userNo}")
    void updateForRevoke(User user);




    @Select("SELECT ui.*, p.`company_name` FROM `user_invitation` ui INNER JOIN `partner` p ON ui.`partner_no` = p.`partner_no` WHERE ui.`user_no` is NULL AND ui.`role` = #{role} AND ui.`invitation_code` = #{invitationCode}")
    User.Invitation findInvitationByCode(@Param("role") User.Role role, @Param("invitationCode") String invitationCode);

    @Update("UPDATE `user_invitation` SET `user_no`=#{userNo}, `signup_date`=NOW() WHERE `user_invitation_no` = #{userInvitationNo}")
    void updateInvitation(@Param("userNo") Long userNo, @Param("userInvitationNo") Long userInvitationNo);

    @Select("SELECT * FROM `user_invitation` WHERE `user_invitation_no` = #{userInvitationNo}")
    User.Invitation findInvitationByNo(Long userInvitationNo);

    @Update("UPDATE `user_invitation` SET `user_no`=NULL, `signup_date`=NULL WHERE `user_no` = #{userNo}")
    void updateInvitationForRevoke(User user);

}
