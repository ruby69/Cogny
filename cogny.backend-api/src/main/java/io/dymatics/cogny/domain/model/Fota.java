package io.dymatics.cogny.domain.model;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Fota implements Serializable {
    private static final long serialVersionUID = -5183426764744170944L;

    public enum Type {
        F, T
    }

    private Long fotaNo;
    private Type type;
    private String version;
    @JsonIgnore private boolean enabled;
    private String url;
    @JsonIgnore private Date regDate;
}
