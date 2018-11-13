package io.dymatics.cogny.staff.service;

import io.dymatics.cogny.domain.model.Page;
import io.dymatics.cogny.domain.model.User;

public interface UserService {
    Object populateUsers(Page page);

    User getUser(Long userNo);

    Object getUserEnums();
    
    boolean checkEmailDup(String email);

    Object saveUser(User user);

    User signUpUser(User user, String invitationCode);

    User deleteUser(Long UserNo);
}
