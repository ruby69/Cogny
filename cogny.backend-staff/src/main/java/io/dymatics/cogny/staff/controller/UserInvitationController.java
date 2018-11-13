package io.dymatics.cogny.staff.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.dymatics.cogny.domain.model.CognyStatus;
import io.dymatics.cogny.domain.model.Page;
import io.dymatics.cogny.domain.model.User;
import io.dymatics.cogny.domain.model.UserInvitation;
import io.dymatics.cogny.staff.service.UserInvitationService;

@RestController
public class UserInvitationController {
    @Autowired private UserInvitationService userInvitationService;
    
    @RequestMapping(value = "invitation", method = RequestMethod.GET)
    public Object getList(@AuthenticationPrincipal User currentUser, Page page) {
        if(currentUser.isAuthorized(page.getPartnerNo())) {
            page.setCurrentUser(currentUser);
            page.setAuthorizedPartnerNo(currentUser.getAuthorizedPartnerNo(page.getPartnerNo()));
            return userInvitationService.populateUserInvitationList(page);
        } else {
            return CognyStatus.UNAUTHORIZED_PARAMETERS.toEntityMap();
        }
    }

    @RequestMapping(value = "invitation/list", method = {RequestMethod.POST, RequestMethod.PUT})
    public Object saveList(@AuthenticationPrincipal User currentUser, @RequestBody UserInvitation.Form form) throws Exception{
        if(currentUser.isAuthorized(form.getPartnerNo())) {
            form.setRegUserNo(currentUser.getUserNo());
            return userInvitationService.saveUserInvitationList(form);
        } else {
            return CognyStatus.UNAUTHORIZED_PARAMETERS.toEntityMap();
        }
    }
    @RequestMapping(value = "invitation/{userInvitationNo}", method = RequestMethod.DELETE)
    public Object delete(@AuthenticationPrincipal User currentUser, @PathVariable Long userInvitationNo) {
        if(!currentUser.isAuthorized(userInvitationService.getUserInvitation(userInvitationNo).getPartnerNo())) return CognyStatus.UNAUTHORIZED_PARAMETERS.toEntityMap();

        UserInvitation userInvitation = userInvitationService.deleteUserInvitation(userInvitationNo);
        
        if(userInvitation == null || userInvitation.getEnabled() == false) {
            return CognyStatus.OK.toEntityMap();
        } else {
            return CognyStatus.UNKNOWN_ERROR.toEntityMap();
        }
    }
}
