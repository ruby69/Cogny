package io.dymatics.cogny.staff.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;

import io.dymatics.cogny.domain.model.EnumToMap;
import io.dymatics.cogny.domain.model.Page;
import io.dymatics.cogny.domain.model.User;
import io.dymatics.cogny.domain.model.User.Role;
import io.dymatics.cogny.domain.model.User.UserStatus;
import io.dymatics.cogny.domain.model.UserInvitation;
import io.dymatics.cogny.domain.persist.UserInvitationRepository;
import io.dymatics.cogny.domain.persist.UserRepository;
import io.dymatics.cogny.staff.service.UserService;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    @Autowired private UserRepository userRepository;
    @Autowired private UserInvitationRepository userInvitationRepository;

    @Override
    public Object populateUsers(Page page) {
        page.setTotal(userRepository.countByPage(page));
        page.setContents(userRepository.findByPage(page));
        return page;
    }

    @Override
    public User getUser(Long userNo) {
        return userRepository.findByNo(userNo);
    }

    @Override
    public Object getUserEnums() {
        Map<String, Object> returnEnums = new HashMap<String, Object>();
        returnEnums.put("userStatusEnums", EnumToMap.getEnumList(UserStatus.values()));
        returnEnums.put("roleEnums", EnumToMap.getEnumList(Role.values()));
        return returnEnums;
    }
    
    @Override 
    public boolean checkEmailDup(String email) {
        // firebase 사용자 조회
        // TODO 사용안함
        boolean isEmailDup = true;
        
        try {
            FirebaseAuth.getInstance().getUserByEmail(email);
            isEmailDup = true;
        } catch (FirebaseAuthException e) {
            if(e.getErrorCode().equals("user-not-found")) {
                isEmailDup = false;
            } else {
                isEmailDup = true;
                e.printStackTrace();
            }
        } catch (Exception e) {
            isEmailDup = true;
            e.printStackTrace();
        }
        if(isEmailDup) return true;

        // cogny 사용자 조회
        if(userRepository.findByEmail(email).size() > 0) return true;

        return false;
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public Object saveUser(User user) {
        if (user.getUserNo() != null) {
            userRepository.update(user);
        } else {
            try {
            // firebase 가입
            CreateRequest request = new CreateRequest()
                    .setEmail(user.getEmail())
                    .setEmailVerified(true) // TODO : 이메일 가입 인증 처리
                    .setPassword(user.getPassword());
            UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
            user.setUuid(userRecord.getUid());
            userRepository.insert(user);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return getUser(user.getUserNo());
    }
    
    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public User signUpUser(User signUpUser, String invitationCode) {
        UserInvitation userInvitation = userInvitationRepository.findByCode(invitationCode);

        signUpUser.setPartnerNo(userInvitation.getPartnerNo());
        signUpUser.setName(userInvitation.getName());
        signUpUser.setHpNo(userInvitation.getHpNo());
        signUpUser.setRole(userInvitation.getRole());
        signUpUser.setUserStatus(UserStatus.MEMBER);
        userRepository.insert(signUpUser);
        
        userInvitation.setUserNo(signUpUser.getUserNo());
        userInvitationRepository.updateUserNo(userInvitation);
        return signUpUser;
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public User deleteUser(Long userNo) {
        userRepository.delete(userNo);
        return getUser(userNo);
    }
}
