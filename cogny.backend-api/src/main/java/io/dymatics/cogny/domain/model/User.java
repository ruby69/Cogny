package io.dymatics.cogny.domain.model;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class User implements Serializable {
    private static final long serialVersionUID = -4416928019589173613L;

    public enum UserStatus {
        MEMBER, QUIT
    }

    public enum Role {
        ADMIN, PARTNER_MECHANIC, DRIVER, ADMIN_ANALYST
    }

    public enum SignProvider {
        COGNY, GOOGLE, FACEBOOK, KAKAO
    }

    private Long userNo;
    private Long partnerNo;
    private String uuid;
    private SignProvider signProvider;
    private String email;
    private String name;
    private String hpNo;
    @JsonIgnore private Role role;
    @JsonIgnore private UserStatus userStatus;
    private Date regDate;
    @JsonIgnore private Date updDate;
    @JsonIgnore private Date delDate;

    private Partner partner;
    private String partnerName;
    private Long userInvitationNo;

    public User(Long partnerNo, String uuid, SignProvider signProvider, String email, String name, String hpNo) {
        this.partnerNo = partnerNo;
        this.uuid = uuid;
        this.signProvider = signProvider;
        this.email = email;
        this.name = name;
        this.hpNo = hpNo;
    }

    @Data
    @JsonInclude(Include.NON_NULL)
    public static class Form implements Serializable {
        private static final long serialVersionUID = 3880430558044245185L;

        private Long userNo;
        private Long partnerNo;
        private SignProvider signProvider;
        private String email;
        private String name;
        private String hpNo;
        private Role role;

        private String token;
        private Long userInvitationNo;

        public User getUser() {
            User user = new User();
            user.setUserNo(userNo);
            user.setPartnerNo(partnerNo);
            user.setSignProvider(signProvider);
            user.setEmail(email);
            user.setName(name);
            user.setHpNo(hpNo);
            user.setRole(role == null ? Role.DRIVER : role);
            user.setUserInvitationNo(userInvitationNo);
            return user;
        }
    }

    @Data
    @JsonInclude(Include.NON_NULL)
    public static class Invitation implements Serializable {
        private static final long serialVersionUID = -4668599690245314556L;

        private Long userInvitationNo;
        @JsonIgnore private String invitationCode;
        private String name;
        private String hpNo;
        @JsonIgnore private Long userNo;
        private Long partnerNo;
        @JsonIgnore private Role role;
        @JsonIgnore private boolean enabled;
        @JsonIgnore private Date regDate;

        private String companyName;
    }
}
