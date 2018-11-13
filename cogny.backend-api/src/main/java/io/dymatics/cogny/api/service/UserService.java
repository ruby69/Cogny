package io.dymatics.cogny.api.service;

import io.dymatics.cogny.domain.model.MobileDevice.UserMobileDeviceForm;
import io.dymatics.cogny.domain.model.User;

public interface UserService {

    Object signup(User user);

    Object signin(String uuid);

    Object getUser(Long userNo);

    Object getPartnerByCode(String partnerCode);

    Object getPartnerBy(Long partnerNo);

    Object revoke(User user);

    Object saveUserMobileDevice(Long userNo, UserMobileDeviceForm form);

    Object checkInvite(User.Role role, String invitationCode);

}
