package io.dymatics.cognyreport.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Meta implements Serializable {
    private static final long serialVersionUID = 3892300187131830457L;

    private Long metaNo;
    private Long reportAndroid;

    public Long getVersionCode() {
        return reportAndroid;
    }
}
