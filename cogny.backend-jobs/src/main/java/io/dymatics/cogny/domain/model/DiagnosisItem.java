package io.dymatics.cogny.domain.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.dymatics.cogny.domain.model.Diagnosis.DiagnosisResult;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString(exclude = {"serviceMsg"})
@JsonInclude(Include.NON_NULL)
public class DiagnosisItem implements Serializable {
    private static final long serialVersionUID = 4949794315459784504L;

    private Long diagnosisItemNo;
    private Long diagnosisCateNo;
    private String diagnosisCode;
    private String name;
    private String serviceMsg;
    private String memo;
    private Boolean hasDtc;

    @JsonIgnore private Boolean enabled;
    private Date regDate;
    private Date updDate;
    @JsonIgnore private Date delDate;

    private DiagnosisCate diagnosisCate;
    private DiagnosisResult diagnosisResult;
    private List<DiagnosisCriteria> diagnosisCriteriaList;
    private int diagnosisCriteriaSize = 0;

    public void setDiagnosisCriteriaList(List<DiagnosisCriteria> diagnosisCriteriaList) {
        this.diagnosisCriteriaList = diagnosisCriteriaList;
        this.diagnosisCriteriaSize = diagnosisCriteriaList.size();
    }
    
    public boolean isCtr() {
        if(!StringUtils.isEmpty(this.diagnosisCode) && this.diagnosisCode.length() >= 3 && this.diagnosisCode.substring(0, 3).equals("CTR")) {
            return true;
        }
        return false;
    }
    
    public boolean isTir() {
        if(!StringUtils.isEmpty(this.diagnosisCode) && this.diagnosisCode.length() >= 3 && this.diagnosisCode.substring(0, 3).equals("TIR")) {
            return true;
        }
        return false;
    }
    
    public boolean isClt() {
        if(!StringUtils.isEmpty(this.diagnosisCode) && this.diagnosisCode.length() >= 3 && this.diagnosisCode.substring(0, 3).equals("CLT")) {
            return true;
        }
        return false;
    }

    @Data
    @NoArgsConstructor
    @JsonInclude(Include.NON_NULL)
    public static class DiagnosisCateCode implements Serializable {
        private static final long serialVersionUID = 8379377919238452613L;

        private Long diagnosisCateCodeNo;
        private Short cateLevel;
        private String name;
    }

    @Data
    @NoArgsConstructor
    @JsonInclude(Include.NON_NULL)
    public static class DiagnosisCate implements Serializable {
        private static final long serialVersionUID = 1275708562955085605L;

        private Long diagnosisCateNo;
        private Long diagnosisCateCodeNo1;
        private Long diagnosisCateCodeNo2;

        private DiagnosisCateCode diagnosisCateCode1;
        private DiagnosisCateCode diagnosisCateCode2;
    }
}
