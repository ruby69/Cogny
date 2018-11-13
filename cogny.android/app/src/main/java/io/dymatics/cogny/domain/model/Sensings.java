package io.dymatics.cogny.domain.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Sensings implements Serializable {
    private static final long serialVersionUID = 2355500294942773863L;

    private List<SensingLog> sensingLogs;
    private List<DtcRaw> dtcRaws;
}
