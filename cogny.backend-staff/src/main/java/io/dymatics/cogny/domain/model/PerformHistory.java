package io.dymatics.cogny.domain.model;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class PerformHistory implements Serializable {
    private static final long serialVersionUID = 2556666517031479324L;

    public enum Ref {
        DTC, REP, REPM, DIAG
    }

    @JsonIgnore private Long performHistoryNo;
    private String title;
    private String body;
    private Long vehicleNo;
    private Ref ref;
    @JsonIgnore private Long refNo;
    private String dateIdx;
    private Date issuedTime;
    private Date regDate;

    private static PerformHistory instance(Long vehicleNo, String title, String body, Ref ref, Long refNo, Date issuedTime) {
        PerformHistory performHistory = new PerformHistory();
        performHistory.setTitle(title);
        performHistory.setBody(body);
        performHistory.setVehicleNo(vehicleNo);
        performHistory.setRef(ref);
        performHistory.setRefNo(refNo);
        performHistory.setIssuedTime(issuedTime);
        return performHistory;
    }

    public static PerformHistory diagnosis(Long vehicleNo, String title, String body, Long refNo, Date issuedTime) {
        return instance(vehicleNo, title, body, Ref.DIAG, refNo, issuedTime);
    }

    public static PerformHistory dtcHistory(Long vehicleNo, String title, String body, Long refNo, Date issuedTime) {
        return instance(vehicleNo, title, body, Ref.DTC, refNo, issuedTime);
    }

    public static PerformHistory repair(Long vehicleNo, String title, Long refNo) {
        return instance(vehicleNo, title, null, Ref.REP, refNo, null);
    }

    public static PerformHistory repairMsg(Long vehicleNo, String title, Long refNo) {
        return instance(vehicleNo, title, null, Ref.REPM, refNo, null);
    }
}
