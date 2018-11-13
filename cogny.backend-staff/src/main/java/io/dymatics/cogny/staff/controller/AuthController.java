package io.dymatics.cogny.staff.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

import com.google.common.collect.ImmutableMap;
import com.google.firebase.auth.FirebaseAuthException;

import io.dymatics.cogny.domain.model.CognyStatus;
import io.dymatics.cogny.domain.model.User;
import io.dymatics.cogny.staff.service.UserInvitationService;
import io.dymatics.cogny.staff.service.UserService;

@Controller
//@Slf4j
public class AuthController {
    @Autowired private UserService userService;
    @Autowired private UserInvitationService userInvitationService;

    @RequestMapping(value = "/auth/denied", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object deniedJson() {
        return CognyStatus.ACCESS_DENYED.toEntityMap();
    }
    
    @RequestMapping(value = "/auth/unauthenticated")
    @ResponseBody
    public Object unauthenticated() {
        return CognyStatus.UNAUTHENTICATED_USER.toEntityMap();
    }

    @RequestMapping(value = "/auth/profile", method = RequestMethod.GET)
    @ResponseBody
    public Object selectByNo(@AuthenticationPrincipal User currentUser) {
        if(currentUser == null || currentUser.getUserNo() == null) {
            return CognyStatus.UNAUTHENTICATED_USER.toEntityMap();
        } else {
            return currentUser;
        }
    }

    @RequestMapping(value = "/auth/token/singin", method = RequestMethod.POST)
    @ResponseBody
    public Object confirmAuth(@AuthenticationPrincipal User currentUser) throws FirebaseAuthException {
        if(currentUser.getUserNo() == null) {
//            FirebaseAuth.getInstance().deleteUser(currentUser.getUuid());
            return new RedirectView("/auth/signout", false);
        } else {
            return ImmutableMap.of("currentUser", currentUser, "status", CognyStatus.OK.getValue(), "message", CognyStatus.OK.getMessage());
        }
    }

    @RequestMapping(value = "/auth/token/singup/{invitationCode}", method = RequestMethod.POST)
    @ResponseBody
    public Object signUp(@AuthenticationPrincipal User currentUser, @PathVariable String invitationCode) {
        if(currentUser.getUserNo() != null) {
            return CognyStatus.DUPLICATED_USER.toEntityMap();
        }
        currentUser = userService.signUpUser(currentUser, invitationCode);
        return ImmutableMap.of("currentUser", currentUser, "status", CognyStatus.OK.getValue(), "message", CognyStatus.OK.getMessage());
    }
    
    @RequestMapping(value = "/auth/invitation/{invitationCode}", method = RequestMethod.GET)
    @ResponseBody
    public Object getInvitation(@PathVariable String invitationCode) {
        return userInvitationService.getUserInvitationByCode(invitationCode);
    }
}
