package io.dymatics.cogny.domain.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class UserInvitation implements Serializable {
    private static final long serialVersionUID = 8197589942404463887L;

    public enum Role {
        ADMIN(null),
        ADMIN_ANALYST(null),
        DRIVER("https://cogny.page.link/?link=https://app.cogny.net/welcome?code=%s&apn=io.dymatics.cogny"),
        PARTNER_MECHANIC("https://cogny.page.link/?link=https://report.cogny.net/welcome?code=%s&apn=io.dymatics.cognyreport")
        ;

        @Getter private String format;

        Role(String format) {
            this.format = format;
        }

        public boolean isDriverOrMechanic() {
            return this == DRIVER || this == PARTNER_MECHANIC;
        }
    }

    private Long userInvitationNo;
    private String invitationCode;
    private Role role;

}
