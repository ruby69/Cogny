package io.dymatics.cogny.domain.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.dymatics.cogny.domain.model.EnumToMap.EnumValuable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class User implements UserDetails {
    private static final long serialVersionUID = 3569421326022198794L;

    public enum UserStatus implements EnumValuable {
        MEMBER("가입중"), QUIT("탈퇴");
        private final String name;

        private UserStatus(String name) {
            this.name = name;
        }
        @Override
        public String getName() {
            return name;
        }
    }

    public enum Role implements EnumValuable {
        ADMIN("관리자"), PARTNER_MECHANIC("고객사의 정비사"), DRIVER("운전자"), ADMIN_ANALYST("본사 분석가"), UNAUTHENTICATED("미가입사용자");
        private final String name;

        private Role(String name) {
            this.name = name;
        }
        @Override
        public String getName() {
            return name;
        }

        public boolean isDriver() {
            return this == DRIVER;
        }

        public boolean isMechanic() {
            return this == PARTNER_MECHANIC;
        }

        public boolean isDriverOrMechanic() {
            return isDriver() || isMechanic();
        }
    }

    public enum SignProvider {
        COGNY, GOOGLE, FACEBOOK, KAKAO
    }

    private final boolean credentialsNonExpired = true;
    private final boolean accountNonLocked = true;
    private final boolean accountNonExpired = true;
    @JsonIgnore private final String password = null;

    private Long userNo;
    private Long partnerNo;
    @JsonIgnore private String uuid;
    private SignProvider signProvider;
    private String email;
    private String name;
    private String tel;
    private String hpNo;
    private Role role;
    private String roleName;
    @JsonIgnore private UserStatus userStatus;
    @JsonIgnore private String userStatusName;
    private Date regDate;
    private Date updDate;
    @JsonIgnore private Date delDate;

    private Partner partner;

    public User(Long userNo, Long partnerNo, String uuid, SignProvider signProvider, String email, String name, String tel, String hpNo, Role role, UserStatus userStatus) {
        this(partnerNo, uuid, signProvider, email, name, tel, hpNo, role, userStatus);
        this.userNo = userNo;
    }

    public User(Long partnerNo, String uuid, SignProvider signProvider, String email, String name, String tel, String hpNo, Role role, UserStatus userStatus) {
        this.partnerNo = partnerNo;
        this.uuid = uuid;
        this.signProvider = signProvider;
        this.email = email;
        this.name = name;
        this.tel = tel;
        this.hpNo = hpNo;
        this.role = role;
        this.userStatus = userStatus;
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.createAuthorityList(role.name());
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return email;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        if(userStatus == UserStatus.MEMBER) {
            return true;
        } else {
            return false;
        }
    }

    @JsonIgnore
    public void setUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
        this.userStatusName = userStatus.getName();
    }

    public void setRole(Role role) {
        this.role = role;
        this.roleName = role.getName();
    }

    public void setSignProviderByIssuer(String issuer) {
        if(issuer.indexOf("google") > -1) {
            this.signProvider = SignProvider.GOOGLE;
        } else if (issuer.indexOf("facebook") > -1) {
            this.signProvider = SignProvider.FACEBOOK;
        } else {
            this.signProvider = null;
        }
    }

    @JsonIgnore
    public boolean isUserLoginAuthorized() {
        if(this.role == Role.ADMIN || this.role == Role.ADMIN_ANALYST || this.role == Role.PARTNER_MECHANIC) {
            return true;
        } else {
            return false;
        }
    }

    @JsonIgnore
    public boolean isAuthorized(Long requestedPartnerNo) {
        if(this.role == Role.ADMIN || this.role == Role.ADMIN_ANALYST || this.partnerNo == requestedPartnerNo) {
            return true;
        } else {
            return false;
        }
    }

    @JsonIgnore
    public boolean isValidNewStaffRole(Role newUserRole) {
        if (this.role != Role.ADMIN && newUserRole == Role.ADMIN) {
            return false;
        } else {
            return true;
        }
    }

    @JsonIgnore
    public Long getAuthorizedPartnerNo(Long requestedPartnerNo) {
        if((this.role == Role.ADMIN || this.role == Role.ADMIN_ANALYST) && requestedPartnerNo != null) {
            return requestedPartnerNo;
        } else if (this.role == Role.PARTNER_MECHANIC && this.partnerNo == requestedPartnerNo) {
            return requestedPartnerNo;
        } else if (this.role == Role.PARTNER_MECHANIC && requestedPartnerNo == null) {
            return this.partnerNo;
        } else {
            return null;
        }
    }


    @Data
    @JsonInclude(Include.NON_NULL)
    public static class Form implements Serializable {
        private static final long serialVersionUID = 3880430558044245185L;

        private Long userNo;
        private String email;
        private String password;
        private Long partnerNo;
        private String uuid;
        private SignProvider signProvider;
        private String name;
        private String tel;
        private String hpNo;
        private UserStatus userStatus;

        public User getUser() {
            User user = new User();
            user.setUserNo(userNo);
            user.setEmail(email);
            user.setPartnerNo(partnerNo);
            user.setSignProvider(signProvider);
            user.setName(name);
            user.setTel(tel);
            user.setHpNo(hpNo);
            user.setUserStatus(userStatus != null ? userStatus: null);
            return user;
        }
    }
}
