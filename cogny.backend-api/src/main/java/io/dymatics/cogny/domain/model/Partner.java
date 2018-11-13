package io.dymatics.cogny.domain.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class Partner implements Serializable {
    private static final long serialVersionUID = -5288969724139922653L;

    private Long partnerNo;
    private String companyName;
    private int vehicleCount;
}
