package io.dymatics.cogny.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@DatabaseTable(tableName = "ObdDevice")
public class ObdDevice implements Serializable {
    private static final long serialVersionUID = 4427805763798850359L;

    public static final String FIELD_pk = "obdDeviceNo";

    @DatabaseField(columnName = FIELD_pk, id = true, generatedId = false) private Long obdDeviceNo;
    @DatabaseField private String obdSerial;
    @DatabaseField private Date regDate;
}
