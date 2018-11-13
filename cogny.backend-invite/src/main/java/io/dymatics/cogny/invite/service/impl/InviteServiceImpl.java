package io.dymatics.cogny.invite.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.dymatics.cogny.domain.model.UserInvitation;
import io.dymatics.cogny.domain.persist.UserInvitationRepository;
import io.dymatics.cogny.invite.service.InviteService;

@Service
@Transactional(readOnly = true)
public class InviteServiceImpl implements InviteService {

    @Autowired private UserInvitationRepository userInvitationRepository;

    @Override
    public String getRedirectUrl(String code) {
        UserInvitation userInvitation = userInvitationRepository.findByCode(code);
        if (userInvitation != null) {
            UserInvitation.Role role = userInvitation.getRole();
            if (role.isDriverOrMechanic()) {
                return String.format(role.getFormat(), userInvitation.getInvitationCode());
            }
        }
        return null;
    }
}
