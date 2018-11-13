package io.dymatics.cogny.staff.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.dymatics.cogny.domain.model.CognyStatus;
import io.dymatics.cogny.domain.model.Page;
import io.dymatics.cogny.domain.model.User;
import io.dymatics.cogny.domain.model.User.UserStatus;
import io.dymatics.cogny.staff.service.UserService;

@RestController
public class UserController {
    @Autowired private UserService userService;

    @RequestMapping(value = "users", method = RequestMethod.GET)
    public Object select(@AuthenticationPrincipal User currentUser, Page page) {
        if(currentUser.isAuthorized(page.getPartnerNo())) {
            page.setCurrentUser(currentUser);
            page.setAuthorizedPartnerNo(currentUser.getAuthorizedPartnerNo(page.getPartnerNo()));
            return userService.populateUsers(page);
        } else {
            return CognyStatus.UNAUTHORIZED_PARAMETERS.toEntityMap();
        }
    }

    @RequestMapping(value = "users/enums", method = RequestMethod.GET)
    public Object getEnums() {
        return userService.getUserEnums();
    }
    
    @RequestMapping(value = "users/isdupemail", method = RequestMethod.GET)
    public Object checkEmailDup(@RequestParam("email") String email) {
        if(userService.checkEmailDup(email)) {
            return CognyStatus.DUPLICATED_USER_EMAIL.toEntityMap();
        }
        return CognyStatus.OK.toEntityMap();
    }

    @RequestMapping(value = "users", method = { RequestMethod.POST, RequestMethod.PUT })
    public Object save(@AuthenticationPrincipal User currentUser, @RequestBody User.Form form) {
        if(currentUser.isAuthorized(form.getPartnerNo())) {
            return userService.saveUser(form.getUser());
        } else {
            return CognyStatus.UNAUTHORIZED_PARAMETERS.toEntityMap();
        }
    }

    @RequestMapping(value = "users/{userNo}", method = RequestMethod.DELETE)
    public Object delete(@AuthenticationPrincipal User currentUser, @PathVariable Long userNo) {
        if(!currentUser.isAuthorized(userService.getUser(userNo).getPartnerNo())) return CognyStatus.UNAUTHORIZED_PARAMETERS.toEntityMap();
        User user = userService.deleteUser(userNo);
        if(user.getUserStatus() == UserStatus.QUIT) {
            return CognyStatus.OK.toEntityMap();
        } else {
            return CognyStatus.UNKNOWN_ERROR.toEntityMap();
        }
    }
}