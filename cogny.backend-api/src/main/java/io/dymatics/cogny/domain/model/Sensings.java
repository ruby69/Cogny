package io.dymatics.cogny.domain.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Sensings implements Serializable {
    private static final long serialVersionUID = 4363287133013271811L;

    private List<SensingLog> sensingLogs;
    private List<DtcRaw> dtcRaws;
}
