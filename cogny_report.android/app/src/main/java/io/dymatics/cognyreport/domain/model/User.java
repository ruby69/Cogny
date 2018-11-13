package io.dymatics.cognyreport.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
@DatabaseTable(tableName = "User")
@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements Serializable {
    private static final long serialVersionUID = 4136095478647028064L;

    public static final String FIELD_pk = "userNo";

    @DatabaseField(columnName = FIELD_pk, id = true) private Long userNo;
    @DatabaseField private Long partnerNo;
    @DatabaseField private String partnerName;
    @DatabaseField private String signProvider;
    @DatabaseField private String email;
    @DatabaseField private String name;
    @DatabaseField private String hpNo;
    @DatabaseField private Date regDate;

    private String role;
    private String token;
    private Long userInvitationNo;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Invitation implements Serializable {
        private static final long serialVersionUID = -4668599690245314556L;

        private Long userInvitationNo;
        private String name;
        private String hpNo;
        private Long partnerNo;
        private String companyName;
    }
}
