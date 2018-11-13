package io.dymatics.cognyreport.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PerformHistory implements Serializable {
    private static final long serialVersionUID = 2556666517031479324L;

    public enum Ref {
        DTC, REP, REPM, DIAG;

        public boolean isDiagnosisOrDtc() {
            return this == DIAG || this == DTC;
        }
    }

    private String title;
    private String body;
    private Ref ref;
    private Date regDate;
    private Date issuedTime;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Group implements Serializable {
        private static final long serialVersionUID = 6326515573585041149L;
        private String issuedDate;
        private List<PerformHistory> list;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Page implements Serializable {
        private static final long serialVersionUID = -217616745398265380L;

        private int page;
        private int scale;
        private List<Group> contents;
        private boolean hasMore;
    }
}
