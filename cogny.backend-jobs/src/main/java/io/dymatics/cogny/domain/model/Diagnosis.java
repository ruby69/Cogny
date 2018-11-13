package io.dymatics.cogny.domain.model;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.dymatics.cogny.domain.model.EnumToMap.EnumValuable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(Include.NON_NULL)
public class Diagnosis implements Serializable {
    private static final long serialVersionUID = -7149250087561429243L;
    
    private Long diagnosisNo;
    private Long diagnosisHistoryNo;
    private Long vehicleNo;
    private Long driveHistoryNo;
    private Long diagnosisItemNo;
    private DiagnosisResult diagnosisResult;
    private String diagnosisResultName;
    private String diagnosisMsg;
    private Long repairNo;
    private Long dateIdx;
    private Date regDate;
    private Date updDate;
    
    private Vehicle vehicle;
    private DriveHistory driveHistory;
    private DiagnosisItem diagnosisItem;
    private Repair repair;

    public enum DiagnosisResult implements EnumValuable {
        NORMAL("정상"), CAUTION("주의"), FATAL("경고"), NOT_AVAILABLE("지원하지 않음"), UNCERTAIN("판정 보류");
        private final String name;
        private DiagnosisResult(String name) {
            this.name = name;
        }
        @Override
        public String getName() {
            return name;
        }
    }
    
    public void setDiagnosisResult(DiagnosisResult diagnosisResult) {
        this.diagnosisResult = diagnosisResult;
        this.diagnosisResultName = diagnosisResult.getName();
    }
    
    @Data
    @NoArgsConstructor
    @ToString
    @JsonInclude(Include.NON_NULL)
    public static class DiagnosisHistory implements Serializable {
        private static final long serialVersionUID = -2994703290688306854L;
        
        private Long diagnosisHistoryNo;
        private Date startDate;
        private Date endDate;
        private Date recentDiagnosisDate;
        private DiagnosisType diagnosisType;
        private Long driveHistoryCnt;
        
        public enum DiagnosisType {
            BATCH, MANUAL;
        }
    }

    @Data
    @NoArgsConstructor
    @ToString
    @JsonInclude(Include.NON_NULL)
    public static class DiagnosisDriveHistory implements Serializable {
        private static final long serialVersionUID = -4323583978955100115L;
        
        private Long driveHistoryNo;
        private Long lastDiagnosisHistoryNo;
        private Date successDiagnosisDate;
        private Boolean succeeded;
        private Date finalDiagnosisDate;
        
        private Date regDate;
        private Date updDate;

        private DriveHistory driveHistory;
        private DiagnosisHistory diagnosisHistory;
        
        // x초 이내에 진단한 drive_history는 다시 진단하지 않는다.
        private int diagnosisIntervalSec;
        private Date currentDiagnosisTime;
        private boolean finished = false;
    }
}