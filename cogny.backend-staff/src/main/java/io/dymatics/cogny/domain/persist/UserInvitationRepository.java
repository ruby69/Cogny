package io.dymatics.cogny.domain.persist;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import io.dymatics.cogny.domain.model.Page;
import io.dymatics.cogny.domain.model.UserInvitation;

@Mapper
public interface UserInvitationRepository {
    void insert(UserInvitation userInvitation);

    void insertList(UserInvitation.Form form);

    UserInvitation findByNo(Long userInvitationNo);

    int countByPage(Page page);

    List<UserInvitation> findByPage(Page page);

    UserInvitation findByCode(String invitationCode);

    void updateInvitationCode(UserInvitation userInvitation);

    void updateSmsResult(UserInvitation userInvitation);

    void updateUserNo(UserInvitation userInvitation);

    void delete(Long userInvitationNo);

}
