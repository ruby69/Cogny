package io.dymatics.cogny.domain.model;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class Diagnosis implements Serializable {
    private static final long serialVersionUID = -7759315534646626910L;

    public static final Long CATE_IGNITION = 1L;
    public static final Long CATE_FUEL = 2L;
    public static final Long CATE_ENGINE = 3L;
    public static final Long CATE_ENGINE_CONTROL = 4L;
    public static final Long CATE_TIRE = 5L;
    public static final Long CATE_GENERATOR = 6L;
    public static final Long CATE_COOLANT = 7L;

    public enum Result {
        CAUTION, FATAL, NORMAL, NOT_AVAILABLE;

        public boolean isCaution() {
            return this == CAUTION;
        }

        public boolean isFatal() {
            return this == FATAL;
        }

        public boolean isNormal() {
            return this == NORMAL;
        }

        public boolean isNotAvailable() {
            return this == NOT_AVAILABLE;
        }
    }

    private Long diagnosisNo;
    private Long diagnosisHistoryNo;
    private Long vehicleNo;
    private Long driveHistoryNo;
    private Long diagnosisItemNo;
    private Result diagnosisResult;
    private Long repairNo;
    private Date regDate;
    private Date updDate;

    @Data
    public static class Summary implements Serializable {
        private static final long serialVersionUID = -6900651911639110506L;

        private Long diagnosisCateNo;
        private String name;
        private String serviceMsg;
        private Result diagnosisResult;
        private String diagnosisMsg;

        @JsonIgnore
        public boolean isShowDescription() {
            if (diagnosisCateNo.longValue() == CATE_TIRE.longValue()) {
                return diagnosisResult.isCaution() || diagnosisResult.isFatal() || diagnosisResult.isNormal();
            } else {
                return diagnosisResult.isCaution() || diagnosisResult.isFatal();
            }
        }

        @JsonIgnore
        public boolean isTireNormal() {
            return diagnosisCateNo.longValue() == CATE_TIRE.longValue() && diagnosisResult.isNormal();
        }
    }

    @Data
    public static class Category implements Serializable {
        private static final long serialVersionUID = 2090231796672243280L;

        private Long diagnosisCateNo;
        private boolean enabled;
    }
}
