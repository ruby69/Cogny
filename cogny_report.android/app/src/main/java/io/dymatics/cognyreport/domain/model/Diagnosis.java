package io.dymatics.cognyreport.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.ArrayList;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Diagnosis implements Serializable {
    private static final long serialVersionUID = -7759315534646626910L;

    public static final Long CATE_IGNITION = 1L;
    public static final Long CATE_FUEL = 2L;
    public static final Long CATE_ENGINE = 3L;
    public static final Long CATE_ENGINE_CONTROL = 4L;
    public static final Long CATE_TIRE = 5L;
    public static final Long CATE_GENERATOR = 6L;
    public static final Long CATE_COOLANT = 7L;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Summary implements Serializable {
        private static final long serialVersionUID = -6900651911639110506L;

        public enum DiagnosisResult {
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

        private Long diagnosisCateNo;
        private String name;
        private String serviceMsg;
        private DiagnosisResult diagnosisResult;
        private String diagnosisMsg;

        public boolean isShowDescription() {
            if (diagnosisCateNo.longValue() == CATE_TIRE.longValue()) {
                return diagnosisResult.isCaution() || diagnosisResult.isFatal() || diagnosisResult.isNormal();
            } else {
                return diagnosisResult.isCaution() || diagnosisResult.isFatal();
            }
        }

        public boolean isTireNormal() {
            return diagnosisCateNo.longValue() == CATE_TIRE.longValue() && diagnosisResult.isNormal();
        }
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Category implements Serializable {
        private static final long serialVersionUID = 2090231796672243280L;

        private Long diagnosisCateNo;
        private boolean enabled;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Categories extends ArrayList<Category> {
        private static final long serialVersionUID = -465259253434282709L;
    }
}
