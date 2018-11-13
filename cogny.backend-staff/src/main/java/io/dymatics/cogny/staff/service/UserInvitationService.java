package io.dymatics.cogny.staff.service;

import io.dymatics.cogny.domain.model.Page;
import io.dymatics.cogny.domain.model.UserInvitation;

public interface UserInvitationService {
    Object populateUserInvitationList(Page page);

    UserInvitation getUserInvitation(Long userInvitationNo);

    UserInvitation getUserInvitationByCode(String invitationCode);

    Object saveUserInvitationList(UserInvitation.Form form);

    UserInvitation deleteUserInvitation(Long userInvitationNo);
}
