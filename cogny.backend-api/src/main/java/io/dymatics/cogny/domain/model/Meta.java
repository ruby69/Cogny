package io.dymatics.cogny.domain.model;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class Meta implements Serializable {
    private static final long serialVersionUID = 9007597443269486877L;

    private Long metaNo;
    private Long cognyAndroid;
    private Long cognyIos;
    private Long reportAndroid;
    private Long reportIos;
    @JsonIgnore private Date regDate;
}
