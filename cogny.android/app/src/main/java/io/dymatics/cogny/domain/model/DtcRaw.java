package io.dymatics.cogny.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import io.dymatics.cogny.Constants;
import lombok.Data;
import lombok.ToString;

@Data
//@ToString(of = {"dtcRawUid", "code", "state"})
@DatabaseTable(tableName = "DtcRaw")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DtcRaw implements Serializable {
    private static final long serialVersionUID = -852420584504609179L;

    public static final String FIELD_pk = "dtcRawUid";
    public static final String FIELD_uploaded = "uploaded";
    public static final String FIELD_driveHistoryNo = "driveHistoryNo";
    public static final String FIELD_code = "code";

    @JsonIgnore @DatabaseField(columnName = FIELD_pk, generatedId = true) private Long dtcRawUid;
    @DatabaseField private Long vehicleNo;
    @DatabaseField private Long obdDeviceNo;
    @DatabaseField(index = true) private Long driveHistoryNo;
    @DatabaseField private long dtcSeq;
    @DatabaseField private long dtcIssuedTime;
    @DatabaseField private long dtcUpdatedTime;
    @DatabaseField private String code;
    @DatabaseField private String state;
    @DatabaseField(defaultValue = "false") private boolean uploaded;

    public void populate(String str) {
        String[] temp = StringUtils.split(str, ",");
        Date date = getDate(temp[1]);
        dtcSeq = Long.parseLong(temp[0]);
        dtcIssuedTime = date.getTime();
        dtcUpdatedTime = date.getTime();
        code = temp[2];
        state = temp[3];
    }

    private Date getDate(String time) {
        try {
            return Constants.FORMAT_DATE_YMD_OBD.parse(time);
        } catch (ParseException e) {
            return new Date();
        }
    }
}
