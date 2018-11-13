package io.dymatics.cogny.staff.service.impl;

import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.dymatics.cogny.domain.model.Page;
import io.dymatics.cogny.domain.model.Sms;
import io.dymatics.cogny.domain.model.UserInvitation;
import io.dymatics.cogny.domain.persist.UserInvitationRepository;
import io.dymatics.cogny.staff.service.ExternalApiService;
import io.dymatics.cogny.staff.service.UserInvitationService;

@Service
@Transactional(readOnly = true)
public class UserInvitationServiceImpl implements UserInvitationService {
    @Autowired private UserInvitationRepository userInvitationRepository;
    @Autowired private ExternalApiService externalApiService;

    @Override
    public Object populateUserInvitationList(Page page) {
        page.setTotal(userInvitationRepository.countByPage(page));
        page.setContents(userInvitationRepository.findByPage(page));
        return page;
    }

    @Override
    public UserInvitation getUserInvitation(Long userInvitationNo) {
        return userInvitationRepository.findByNo(userInvitationNo);
    }

    @Override
    public UserInvitation getUserInvitationByCode(String invitationCode) {
        return userInvitationRepository.findByCode(invitationCode);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public Object saveUserInvitationList(UserInvitation.Form form) {
        for (UserInvitation userInvitation : form.getUserInvitationList()) {
            userInvitation.setPartnerNo(form.getPartnerNo());
            userInvitation.setRole(form.getRole());
            userInvitation.setRegUserNo(form.getRegUserNo());
            userInvitationRepository.insert(userInvitation);

            userInvitation.setInvitationCode(getInvitationCode(userInvitation.getUserInvitationNo()));
            userInvitationRepository.updateInvitationCode(userInvitation);

            userInvitation = getUserInvitation(userInvitation.getUserInvitationNo());
            userInvitation = invitationSms(userInvitation); // TODO sms 발송 활성화
            userInvitationRepository.updateSmsResult(userInvitation);
        }
        return form;
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public UserInvitation deleteUserInvitation(Long userInvitationNo) {
        userInvitationRepository.delete(userInvitationNo);
        return getUserInvitation(userInvitationNo);
    }

    private String getInvitationCode(Long userInvitationNo) {
        Encoder encoder = Base64.getEncoder();
        Random rnd = new Random();
        String randomHeader = String.valueOf((char) ((rnd.nextInt(58)) + 65));
        String userInvitationCodeSeed = randomHeader + Long.toHexString(userInvitationNo);
        byte[] invitationByte = encoder.encode(userInvitationCodeSeed.getBytes());
        String rtnCode = new String(invitationByte);
        return rtnCode.replaceAll("=", "");
    }

    private static final String INVITE_MESSAGE_FORMAT = "[%s-코그니] %s님, 아래 경로에서 차량진단 앱  코그니에 가입하시고 앱을 다운로드 받으세요.\r\n%s";
    private static final String INVITE_TITLE_FORMAT = "[%s-코그니] 가입초대";
    private UserInvitation invitationSms(UserInvitation userInvitation) {
        if (!userInvitation.getRole().isDriverOrMechanic()) {
            throw new RuntimeException("유효하지 않은 권한의 사용자를 초대하였습니다.");
        }

        String link = externalApiService.inviteLink(userInvitation.getInvitationCode());
        String invitationMsg = String.format(INVITE_MESSAGE_FORMAT, userInvitation.getPartner().getCompanyName(), userInvitation.getName(), link);
        String invitationTitle = String.format(INVITE_TITLE_FORMAT, userInvitation.getPartner().getCompanyName());
        Sms.Status smsResponse = externalApiService.sendSms(invitationTitle, invitationMsg, userInvitation.getHpNo());
        userInvitation.setSmsResponse(smsResponse.getResponseCode());
        userInvitation.setSmsStatus(smsResponse.getStatusCode());
        return userInvitation;
    }
}
