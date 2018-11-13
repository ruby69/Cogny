package io.dymatics.cogny.api.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.dymatics.cogny.api.service.UserService;
import io.dymatics.cogny.domain.model.MobileDevice;
import io.dymatics.cogny.domain.model.MobileDevice.UserMobileDeviceForm;
import io.dymatics.cogny.domain.model.User;
import io.dymatics.cogny.domain.persist.MobileDeviceRepository;
import io.dymatics.cogny.domain.persist.PartnerRepository;
import io.dymatics.cogny.domain.persist.UserMobileDeviceRepository;
import io.dymatics.cogny.domain.persist.UserRepository;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    @Autowired private UserRepository userRepository;
    @Autowired private PartnerRepository partnerRepository;
    @Autowired private MobileDeviceRepository mobileDeviceRepository;
    @Autowired private UserMobileDeviceRepository userMobileDeviceRepository;

    @Override
    public Object checkInvite(User.Role role, String invitationCode) {
        return userRepository.findInvitationByCode(role, invitationCode);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public Object signup(User user) {
        User find = userRepository.findByUuid(user.getUuid());
        if (find == null) {
            userRepository.insert(user);
        } else {
            user.setUserNo(find.getUserNo());
            userRepository.update(user);
        }

        Long userInvitationNo = user.getUserInvitationNo();
        if (userInvitationNo != null) {
            userRepository.updateInvitation(user.getUserNo(), userInvitationNo);

            User.Invitation invitation = userRepository.findInvitationByNo(userInvitationNo);
            userRepository.updateUserName(invitation.getName(), user.getUserNo());
        }

        return getUser(user.getUserNo());
    }

    @Override
    public Object signin(String uuid) {
        return userRepository.findByUuid(uuid);
    }

    @Override
    public User getUser(Long userNo) {
        return userRepository.findByNo(userNo);
    }

    @Override
    public Object getPartnerByCode(String partnerCode) {
        return partnerRepository.findByCode(partnerCode);
    }

    @Override
    public Object getPartnerBy(Long partnerNo) {
        return partnerRepository.findBy(partnerNo);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public Object revoke(User user) {
        userRepository.updateInvitationForRevoke(user);
        userRepository.updateForRevoke(user);
        return getUser(user.getUserNo());
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public Object saveUserMobileDevice(Long userNo, UserMobileDeviceForm form) {

        MobileDevice mobileDevice = form.getMobileDevice();
        MobileDevice.UserMobileDevice userMobileDevice = form.getUserMobileDevice();
        userMobileDevice.setUserNo(userNo);

        MobileDevice findDevice = mobileDeviceRepository.findByUuid(mobileDevice.getUuid());
        if (findDevice == null) {
            mobileDeviceRepository.insert(mobileDevice);
            userMobileDevice.setMobileDeviceNo(mobileDevice.getMobileDeviceNo());
        } else {
            userMobileDevice.setMobileDeviceNo(findDevice.getMobileDeviceNo());
        }

        MobileDevice.UserMobileDevice findUserDevice = userMobileDeviceRepository.findByUserAndMobileDevice(userMobileDevice);
        if (findUserDevice == null) {
            userMobileDeviceRepository.insert(userMobileDevice);
            return userMobileDevice;
        } else {
            findUserDevice.setMobileNumber(userMobileDevice.getMobileNumber());
            findUserDevice.setFcmToken(userMobileDevice.getFcmToken());
            findUserDevice.setPushAgree(userMobileDevice.isPushAgree());
            findUserDevice.setPushAgreeDate(userMobileDevice.getPushAgreeDate());
            userMobileDeviceRepository.update(findUserDevice);
            return findUserDevice;
        }
    }

}
