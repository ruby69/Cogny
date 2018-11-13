package io.dymatics.cogny.domain.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class UserInvitation implements Serializable {
    private static final long serialVersionUID = -3972548719813405515L;

    private Long userInvitationNo;
    private String invitationCode;
    private String name;
    private String hpNo;
    private Long userNo;
    private Long partnerNo;
    private User.Role role;
    private String roleName;
    @JsonIgnore private Boolean enabled;
    private Long regUserNo;
    @JsonIgnore private String smsResponse;
    @JsonIgnore private String smsStatus;
    private Date regDate;
    private Date signUpDate;
    @JsonIgnore private Date delDate;

    private User signUpUser;
    private Partner partner;
    private User regUser;

    public void setRole(User.Role role) {
        this.role = role;
        this.roleName = role.getName();
    }
    
    @Data
    @JsonInclude(Include.NON_NULL)
    public static class Form implements Serializable {
        private static final long serialVersionUID = 1035980940907980010L;

        private Long partnerNo;
        private User.Role role;
        @JsonIgnore private Long regUserNo;

        private List<UserInvitation> userInvitationList;
    }
}
