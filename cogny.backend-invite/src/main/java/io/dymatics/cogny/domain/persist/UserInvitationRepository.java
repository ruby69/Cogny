package io.dymatics.cogny.domain.persist;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import io.dymatics.cogny.domain.model.UserInvitation;

@Mapper
public interface UserInvitationRepository {

    @Select("SELECT * FROM `user_invitation` WHERE `invitation_code` = #{value} AND `enabled` = 1")
    UserInvitation findByCode(String invitationCode);

}
