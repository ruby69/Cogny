package io.dymatics.cogny.domain.model;

import java.io.Serializable;
import java.util.Date;

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
public class MobileDevice implements Serializable {
    private static final long serialVersionUID = 5774259491138196224L;

    public enum OS {
        ANDROID, IOS, WIN
    }

    private Long mobileDeviceNo;
    private String uuid;
    private OS os;
    private Date regDate;

    @Data
    @JsonInclude(Include.NON_NULL)
    public static class UserMobileDevice implements Serializable {
        private static final long serialVersionUID = 2633831848562664607L;

        private Long userMobileDeviceNo;
        @JsonIgnore private Long userNo;
        @JsonIgnore private Long partnerNo;
        private Long mobileDeviceNo;
        private String mobileNumber;
        @JsonIgnore private String fcmToken;
        private boolean pushAgree;
        private Date pushAgreeDate;
    }

    @Data
    @JsonInclude(Include.NON_NULL)
    public static class UserMobileDeviceForm implements Serializable {
        private static final long serialVersionUID = -1107350516699138955L;

        private String uuid;
        private OS os;
        private String mobileNumber;
        private String fcmToken;
        private boolean pushAgree;

        public MobileDevice getMobileDevice() {
            MobileDevice mobileDevice = new MobileDevice();
            mobileDevice.setUuid(uuid);
            mobileDevice.setOs(os);
            return mobileDevice;
        }

        public UserMobileDevice getUserMobileDevice() {
            UserMobileDevice userMobileDevice = new UserMobileDevice();
            userMobileDevice.setMobileNumber(mobileNumber);
            userMobileDevice.setFcmToken(fcmToken);
            userMobileDevice.setPushAgree(pushAgree);
            userMobileDevice.setPushAgreeDate(pushAgree ? new Date() : null);
            return userMobileDevice;
        }
    }
}
