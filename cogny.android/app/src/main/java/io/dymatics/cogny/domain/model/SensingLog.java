package io.dymatics.cogny.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(of = {"sensingLogUid", "values"})
@DatabaseTable(tableName = "SensingLog")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SensingLog implements Serializable {
    private static final long serialVersionUID = 8789622471838692856L;

    public static final String FIELD_pk = "sensingLogUid";
    public static final String FIELD_uploaded = "uploaded";

    @JsonIgnore @DatabaseField(columnName = FIELD_pk, generatedId = true) private Long sensingLogUid;
    @DatabaseField private Long vehicleNo;
    @DatabaseField private Long obdDeviceNo;
    @DatabaseField private Long driveHistoryNo;
    @DatabaseField private String values;
    @DatabaseField(defaultValue = "false") private boolean uploaded;
}
