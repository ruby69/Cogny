package io.dymatics.cogny.domain.model;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@NoArgsConstructor
@ToString
@JsonInclude(Include.NON_NULL)
public class DiagnosisCriteria implements Serializable {
    private static final long serialVersionUID = -4269162713656515824L;

    @Getter @Setter private Long diagnosisCriteriaNo;
    @Getter @Setter private DiagnosisSensorType sensorType;

    private int validCnt = 0;
    @Getter @Setter private Integer spdCutoff;
    @Getter @Setter private Integer spdCntCutoff;
    @Getter @Setter private boolean afterSpdCutoff = false;
    private int spdCnt = 0;
    
    @Getter @Setter private Integer cautionCutoff;
    @Getter @Setter private Integer cautionCutoffWithAc;
    @Getter @Setter private Integer cautionCntCutoff;
    private int cautionCnt = 0;
    @Getter @Setter private Integer fatalCutoff;
    @Getter @Setter private Integer fatalCutoffWithAc;
    @Getter @Setter private Integer fatalCntCutoff;
    private int fatalCnt = 0;

    @Getter @Setter private Date regDate;

    @Getter @Setter private Integer guideValue;

    public enum DiagnosisSensorType {
        LPG_FUEL_RAIL_PA,
        INJECTION_TIME,
        OXY_VOLT_S2_DENSE,
        OXY_VOLT_S2_SPARSE,
        TIRE_PRESSURE,
        COOLANT_TEMP,
        BATTERY_VOLT_GNT;
    }
    public void addValidCnt() {
        this.validCnt++;
    }
    public void addSpdCnt() {
        this.spdCnt++;
    }
    public void addCautionCnt() {
        this.cautionCnt++;
    }
    public void addFatalCnt() {
        this.fatalCnt++;
    }
    public boolean isSpdCntValid() {
        if(hasNull(this.spdCntCutoff)) {
            return false;
        } else if(this.spdCnt > this.spdCntCutoff) {
            return true;
        } else {
            return false;
        }
    }
    public boolean isValid() {
        if(this.validCnt > 0) {
            return true;
        } else {
            return false;
        }
    }
    public boolean isCaution() {
        if(hasNull(this.cautionCnt, this.cautionCntCutoff)) {
            return false;
        } else if(this.cautionCnt > this.cautionCntCutoff) {
            return true;
        } else {
            return false;
        }
    }
    public boolean isFatal() {
        if(hasNull(this.fatalCnt, this.fatalCntCutoff)) {
            return false;
        } else if(this.fatalCnt > this.fatalCntCutoff) {
            return true;
        } else {
            return false;
        }
    }
    private boolean hasNull(Object... nullableValues) {
        for(Object nullableValue: nullableValues) {
            if(nullableValue == null) return true;
        }
        return false;
    }
}
