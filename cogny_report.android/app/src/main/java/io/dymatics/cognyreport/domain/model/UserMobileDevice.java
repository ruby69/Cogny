package io.dymatics.cognyreport.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@DatabaseTable(tableName = "UserMobileDevice")
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserMobileDevice implements Serializable {
    private static final long serialVersionUID = 2590132107210785170L;

    public static final String FIELD_pk = "userMobileDeviceNo";

    public enum OS {
        ANDROID, IOS, WIN
    }

    @DatabaseField(columnName = FIELD_pk, id = true) private Long userMobileDeviceNo;
    @DatabaseField private String mobileNumber;
    private String name;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Form implements Serializable {
        private String uuid;
        private OS os;
        private String mobileNumber;
        private String fcmToken;
        private boolean pushAgree;
    }

    public static Form form(String deviceUuid, String mobileNumber, String fcmToken, boolean pushAgree) {
        Form form = new Form();
        form.setUuid(deviceUuid);
        form.setOs(OS.ANDROID);
        form.setMobileNumber(mobileNumber);
        form.setFcmToken(fcmToken);
        form.setPushAgree(pushAgree);
        return form;
    }
}
