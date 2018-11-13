package io.dymatics.cogny.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Partner implements Serializable {
    private static final long serialVersionUID = 377545746835285916L;

    private Long partnerNo;
    private String companyName;
}
