package io.dymatics.cogny.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@DatabaseTable(tableName = "Fota")
public class Fota implements Serializable {
    private static final long serialVersionUID = -1901759063212538097L;

    public enum Type {
        F, T;

        public boolean isFirmware() {
            return this == F;
        }

        public boolean isTable() {
            return this == T;
        }
    }

    public static final String FIELD_pk = "fotaNo";
    public static final String FIELD_type = "type";

    @DatabaseField(columnName = FIELD_pk, id = true, generatedId = false) private Long fotaNo;
    @DatabaseField private Type type;
    @DatabaseField private String version;
    @DatabaseField private String url;
    @DatabaseField private boolean applied;
    @DatabaseField private String filePath;

    public boolean isReady(String localVersion) {
        return filePath != null && !filePath.equals("null") && !filePath.isEmpty() && !applied && !version.equals(localVersion);
    }
}
