package io.dymatics.cogny.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;

import io.dymatics.cogny.api.service.UserService;
import io.dymatics.cogny.domain.model.MobileDevice.UserMobileDeviceForm;
import io.dymatics.cogny.domain.model.User;

@RestController
public class UserController {
    @Autowired private UserService userService;

    private FirebaseToken toFirebaseToken(String token) {
        try {
            return FirebaseAuth.getInstance().verifyIdTokenAsync(token).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping(value = "invite/{role}/{invitationCode}", method = RequestMethod.GET)
    public Object checkInvite(@PathVariable("role") User.Role role, @PathVariable("invitationCode") String invitationCode) {
        return userService.checkInvite(role, invitationCode);
    }

    @RequestMapping(value = "signup", method = { RequestMethod.POST, RequestMethod.PUT })
    public Object signup(@RequestBody User.Form form) {
        User user = form.getUser();
        user.setUuid(toFirebaseToken(form.getToken()).getUid());
        return userService.signup(user);
    }

    @RequestMapping(value = "signin", method = RequestMethod.POST)
    public Object signin(@RequestBody String token) {
        Object find = userService.signin(toFirebaseToken(token.replaceAll("\"", "")).getUid());
        if (find == null) {
            throw new UserNotFoundException();
        }
        return find;
    }

    @RequestMapping(value = "users/check", method = RequestMethod.POST)
    public Object check(@RequestBody String token) {
        return userService.signin(toFirebaseToken(token.replaceAll("\"", "")).getUid());
    }

    @RequestMapping(value = "revoke", method = RequestMethod.POST)
    public Object revoke(@RequestBody String token, User user) {
        if (user.getUuid().equals(toFirebaseToken(token.replaceAll("\"", "")).getUid())) {
            return userService.revoke(user);
        } else {
            return user;
        }
    }

    @RequestMapping(value = "mobiles", method = { RequestMethod.POST, RequestMethod.PUT })
    public Object mobiles(@RequestBody UserMobileDeviceForm form, User user) {
        return userService.saveUserMobileDevice(user.getUserNo(), form);
    }












    @RequestMapping(value = "partners/{partnerCode}", method = RequestMethod.GET)
    public Object partnerByCode(@PathVariable String partnerCode) {
        return userService.getPartnerByCode(partnerCode);
    }

    @RequestMapping(value = "partners", method = RequestMethod.GET)
    public Object partners(User user) {
        return userService.getPartnerBy(user.getPartnerNo());
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Not found user.")
    @ExceptionHandler(value = UserNotFoundException.class)
    public void handleUserNotFoundException() {
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "username already exists.")
    @ExceptionHandler(value = DuplicateKeyException.class)
    public void handleDuplicateKeyException() {
    }

    private static class UserNotFoundException extends RuntimeException {
        private static final long serialVersionUID = -7637495488967838995L;

    }
}
