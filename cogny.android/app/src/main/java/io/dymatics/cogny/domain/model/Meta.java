package io.dymatics.cogny.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Meta implements Serializable {
    private static final long serialVersionUID = -3038851416476918386L;

    private Long metaNo;
    private Long cognyAndroid;

    public Long getVersionCode() {
        return cognyAndroid;
    }
}
