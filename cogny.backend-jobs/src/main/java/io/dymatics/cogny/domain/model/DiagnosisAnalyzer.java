package io.dymatics.cogny.domain.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.dymatics.cogny.domain.model.Diagnosis.DiagnosisResult;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@ToString(exclude = {"sensingRawList"})
public class DiagnosisAnalyzer {
    private Long diagnosisHistoryNo;
    private Long driveHistoryNo;
    private Long vehicleNo;
    private List<SensingRaw> sensingRawList;
    private List<DtcRaw> dtcRawDistinctList;

    private List<DiagnosisItem> diagnosisItemList;

    // sensingRawList의 크기
    private int sensingRawSize = 0;
    // 유효 공회전 횟수
    private int stableIdleCnt = 0;
    // 운전 종료여부
    private boolean finishedDrive = false;

    // 산소센서S2 희박 조건을 위한 time lag x s
    private int o2S2SparseTimeLag;
    // 공회전 또는 AC OFF 이후 안정화 기준 시간 x s
    private int stableSecBefore;
    // 공회전이 종료되고 속도가 0 이상이 된 시점이 x초 이후인 공회전만 유효한 공회전으로 판단한다.
    private int stableSecAfter;
    // 냉각수 온도가 x ℃ 이후의 데이터만 피드백 제어를 시작하는 것으로 판단
    private int validCoolantTemp;

    // 진단결과 저장
    private Map<String, Diagnosis> diagnosisReport = new HashMap<String, Diagnosis>();

    public DiagnosisAnalyzer(int o2S2SparseTimeLag, int stableSecBefore, int stableSecAfter, int validCoolantTemp){
        this.o2S2SparseTimeLag = o2S2SparseTimeLag;
        this.stableSecBefore = stableSecBefore;
        this.stableSecAfter = stableSecAfter;
        this.validCoolantTemp = validCoolantTemp;
    }
    
    public void execute(DriveHistory driveHistory, List<SensingRaw> sensingRawList, List<DiagnosisItem> diagnosisItemList, List<DtcRaw> dtcRawDistinctList) throws Exception {
        if(sensingRawList.isEmpty()) return;
        this.sensingRawList = sensingRawList;
        this.vehicleNo = driveHistory.getVehicleNo();
        this.driveHistoryNo = driveHistory.getDriveHistoryNo();
        this.diagnosisItemList = diagnosisItemList;
        this.dtcRawDistinctList = dtcRawDistinctList;

        this.makeDiagnosis();
        this.setDiagnosisReport();
    }

    public Diagnosis getDiagnosisReport(String diagnosisCode) {
        if(!this.diagnosisReport.containsKey(diagnosisCode)) {
            log.error("invalid diagnosisCode : " + this.toString());
            return null;
        } else {
            return this.diagnosisReport.get(diagnosisCode);
        }
    }

    private void makeDiagnosis() throws Exception {
        for(int i=0; i < sensingRawList.size(); i++) {
            sensingRawSize++;
            SensingRaw sensingRaw = sensingRawList.get(i);
            if(!this.vehicleNo.equals(sensingRaw.getVehicleNo())) {
                log.error("Invalid vehicle_no : " + this.toString());
                throw new RuntimeException("Invalid vehicle_no");
            }

            if(!this.driveHistoryNo.equals(sensingRaw.getDriveHistoryNo())) {
                log.error("Invalid drive_history_no : " + this.toString());
                throw new RuntimeException("Invalid drive_history_no");
            }

            // 분석 대상 유효한 공회전 회수 카운트
            if(isStableIdle(i)) stableIdleCnt++;

            // 각 진단항목(diagnosisItem) 별로 진단 기준(diagnosisCriteria)을 만족하는 경우 진단 횟수를 증가시킨다.
            for(DiagnosisItem diagnosisItem : this.diagnosisItemList) {
                for(DiagnosisCriteria diagnosisCriteria : diagnosisItem.getDiagnosisCriteriaList()) {
                    switch (diagnosisCriteria.getSensorType()) {
                        case LPG_FUEL_RAIL_PA:
                            if(hasNull(sensingRaw.getLpgFuelRailPaVolt())) break;
                            diagnosisCriteria.addValidCnt();
                            if(diagnosisCriteria.getCautionCutoff() != null
                                    && sensingRaw.getLpgFuelRailPaVolt() >0
                                    && sensingRaw.getLpgFuelRailPaVolt() < diagnosisCriteria.getCautionCutoff()) diagnosisCriteria.addCautionCnt();
                            if(diagnosisCriteria.getFatalCutoff() != null
                                    && sensingRaw.getLpgFuelRailPaVolt() >0
                                    && sensingRaw.getLpgFuelRailPaVolt() < diagnosisCriteria.getFatalCutoff()) diagnosisCriteria.addFatalCnt();
                            break;
                        case TIRE_PRESSURE:
                            /*
                             * 타이어 압력을 측정하지 못하는 차종 판정
                             * 60 km/h를 넘으면 타이어 압력의 측정이 가능하다
                             */
                            if(sensingRaw.getVehicleSpd() != null && diagnosisCriteria.getSpdCutoff() != null
                                && sensingRaw.getVehicleSpd() >= diagnosisCriteria.getSpdCutoff()) diagnosisCriteria.addSpdCnt();

                            if(hasNull(sensingRaw.getVehicleSpd(),
                                    sensingRaw.getTirePressure1(),
                                    sensingRaw.getTirePressure2(),
                                    sensingRaw.getTirePressure3(),
                                    sensingRaw.getTirePressure4())) break;
                            diagnosisCriteria.addValidCnt();

                            // item별로 타이어 측정
                            Integer tirePressure;
                            // 측정대상 타이어 결정
                            if(diagnosisItem.getDiagnosisCode().equals("TIR001")) {
                                tirePressure = sensingRaw.getTirePressure1();
                            } else if(diagnosisItem.getDiagnosisCode().equals("TIR002")) {
                                tirePressure = sensingRaw.getTirePressure2();
                            } else if(diagnosisItem.getDiagnosisCode().equals("TIR003")) {
                                tirePressure = sensingRaw.getTirePressure3();
                            } else if(diagnosisItem.getDiagnosisCode().equals("TIR004")) {
                                tirePressure = sensingRaw.getTirePressure4();
                            } else {
                                log.error("Invalid tire item : " + this.toString());
//                                throw new Exception("Invalid tire item");
                                throw new RuntimeException("Invalid tire item");
                            }
                            // 타이어 진단
                            if(diagnosisCriteria.getCautionCutoff() != null
                                    && tirePressure <= diagnosisCriteria.getCautionCutoff()) diagnosisCriteria.addCautionCnt();
                            if(diagnosisCriteria.getFatalCutoff() != null
                                    && tirePressure <= diagnosisCriteria.getFatalCutoff()) diagnosisCriteria.addFatalCnt();
                            // 타이어 진단 완료 이후 사용자에게 안내하기 위해 타이어 압력을 저장한다.
                            if(diagnosisCriteria.getGuideValue() == null || diagnosisCriteria.getGuideValue() > tirePressure) {
                                diagnosisCriteria.setGuideValue(tirePressure);
                            }
                            break;
                        case COOLANT_TEMP:
                            if(hasNull(sensingRaw.getCoolantTemp())) break;
                            diagnosisCriteria.addValidCnt();
                            if(diagnosisCriteria.getCautionCutoff() != null
                                    && sensingRaw.getCoolantTemp() > diagnosisCriteria.getCautionCutoff()) diagnosisCriteria.addCautionCnt();
                            if(diagnosisCriteria.getFatalCutoff() != null
                                    && sensingRaw.getCoolantTemp() > diagnosisCriteria.getFatalCutoff()) diagnosisCriteria.addFatalCnt();
                            // 냉각수온 진단 완료 이후 사용자에게 안내하기 위해 냉각수온을 저장한다.
                            if(diagnosisCriteria.getGuideValue() == null || diagnosisCriteria.getGuideValue() < sensingRaw.getCoolantTemp()) {
                                diagnosisCriteria.setGuideValue(sensingRaw.getCoolantTemp());
                            }
                            break;
                        case BATTERY_VOLT_GNT:
                            if(hasNull(sensingRaw.getBatteryVolt())) break;
                            diagnosisCriteria.addValidCnt();
                            if(diagnosisCriteria.getCautionCutoff() != null
                                    && sensingRaw.getBatteryVolt() > 0
                                    && sensingRaw.getBatteryVolt() < diagnosisCriteria.getCautionCutoff()) diagnosisCriteria.addCautionCnt();
                            if(diagnosisCriteria.getFatalCutoff() != null
                                    && sensingRaw.getBatteryVolt() < diagnosisCriteria.getFatalCutoff()) diagnosisCriteria.addFatalCnt();
                            break;
                        case OXY_VOLT_S2_DENSE:
                            if(hasNull(sensingRaw.getCoolantTemp(), sensingRaw.getOxyVoltS2())) break;
                            diagnosisCriteria.addValidCnt();
                            if(!isStableIdle(i)) break;
                            if(diagnosisCriteria.getCautionCutoff() != null
                                    && sensingRaw.getOxyVoltS2() > diagnosisCriteria.getCautionCutoff()) diagnosisCriteria.addCautionCnt();
                            if(diagnosisCriteria.getFatalCutoff() != null
                                    && sensingRaw.getOxyVoltS2() > diagnosisCriteria.getFatalCutoff()) diagnosisCriteria.addFatalCnt();
                            break;
                        case OXY_VOLT_S2_SPARSE:
                            // 차량 속도가 x km/h를 넘긴 이후부터 측정한다.
                            if(!diagnosisCriteria.isAfterSpdCutoff()) {
                                if(sensingRaw.getVehicleSpd() != null && sensingRaw.getVehicleSpd() > diagnosisCriteria.getSpdCutoff()) {
                                    diagnosisCriteria.setAfterSpdCutoff(true);
                                } else {
                                    break;
                                }
                            }
                            if(hasNull(sensingRaw.getVehicleSpd(), sensingRaw.getCoolantTemp(), sensingRaw.getOxyVoltS2())) break;
                            
                            diagnosisCriteria.addValidCnt();
                            if(!isStableIdle(i)) break;
                            if(diagnosisCriteria.getCautionCutoff() != null
                                    && this.isOxySparse(i, diagnosisCriteria.getCautionCutoff())) diagnosisCriteria.addCautionCnt();
                            if(diagnosisCriteria.getFatalCutoff() != null
                                    && this.isOxySparse(i, diagnosisCriteria.getFatalCutoff())) diagnosisCriteria.addFatalCnt();
                            break;
                        case INJECTION_TIME:
                            if(hasNull(sensingRaw.getCoolantTemp(),
                                        sensingRaw.getInjectionTimeC1(),
                                        sensingRaw.getAcCompressorOn()
                                        )) break;
                            diagnosisCriteria.addValidCnt();
                            if(!isStableIdle(i) || hasNull(diagnosisCriteria.getCautionCutoffWithAc(),
                                                            diagnosisCriteria.getFatalCutoffWithAc(),
                                                            diagnosisCriteria.getCautionCutoff(),
                                                            diagnosisCriteria.getFatalCutoff())) break;
                            int cautionCutoff, fatalCutoff;
                            if(sensingRaw.getAcCompressorOn() > 0) {
                                cautionCutoff = diagnosisCriteria.getCautionCutoffWithAc();
                                fatalCutoff = diagnosisCriteria.getFatalCutoffWithAc();
                            } else {
                                cautionCutoff = diagnosisCriteria.getCautionCutoff();
                                fatalCutoff = diagnosisCriteria.getFatalCutoff();
                            }
                            if(sensingRaw.getInjectionTimeC1() > cautionCutoff) diagnosisCriteria.addCautionCnt();
                            if(diagnosisCriteria.getFatalCutoff() != null
                                    && sensingRaw.getInjectionTimeC1() > fatalCutoff) diagnosisCriteria.addFatalCnt();
                            break;
                        default:
                            log.error("Invalid diagnosis.sensorType : " + this.toString());
                            throw new RuntimeException("Invalid diagnosis.sensorType");
                    }
                }
            }
        }
    }

    private void setDiagnosisReport() {
        for(DiagnosisItem diagnosisItem: this.diagnosisItemList) {
            Diagnosis diagnosis = new Diagnosis();
            diagnosis.setVehicleNo(vehicleNo);
            diagnosis.setDriveHistoryNo(driveHistoryNo);
            diagnosis.setDiagnosisItemNo(diagnosisItem.getDiagnosisItemNo());
            if(diagnosisItem.isCtr()) {
                // 엔진제어인 경우
                diagnosis.setDiagnosisResult(this.getDiagnosisResultByDtcList());
                if(diagnosis.getDiagnosisResult() == DiagnosisResult.FATAL) diagnosis.setDiagnosisMsg("엔진경고");
            } else if (diagnosisItem.isTir()) {
                // 타이어인 경우
                diagnosis.setDiagnosisResult(this.getDiagnosisResultOfTirePressure(diagnosisItem));
                if(diagnosis.getDiagnosisResult() == DiagnosisResult.CAUTION || diagnosis.getDiagnosisResult() == DiagnosisResult.FATAL || diagnosis.getDiagnosisResult() == DiagnosisResult.NORMAL ) {
                    Integer minTirePressure = null;
                    for(DiagnosisCriteria diagnosisCriteria: diagnosisItem.getDiagnosisCriteriaList()) {
                        if(minTirePressure == null || diagnosisCriteria.getGuideValue() < minTirePressure) {
                            minTirePressure = diagnosisCriteria.getGuideValue();
                        }
                    }
                    diagnosis.setDiagnosisMsg("" + minTirePressure + " psi");
                }
            } else if (diagnosisItem.isClt()) {
                // 냉각수온인 경우
                diagnosis.setDiagnosisResult(this.getDiagnosisResultByDiagnosisCriteria(diagnosisItem.getDiagnosisCriteriaList()));
                if(diagnosis.getDiagnosisResult() == DiagnosisResult.CAUTION || diagnosis.getDiagnosisResult() == DiagnosisResult.FATAL || diagnosis.getDiagnosisResult() == DiagnosisResult.NORMAL ) {
                    Integer maxCoolantTemp = null;
                    for(DiagnosisCriteria diagnosisCriteria: diagnosisItem.getDiagnosisCriteriaList()) {
                        if(maxCoolantTemp == null || diagnosisCriteria.getGuideValue() > maxCoolantTemp) {
                            maxCoolantTemp = diagnosisCriteria.getGuideValue();
                        }
                    }
                    diagnosis.setDiagnosisMsg("" + maxCoolantTemp + "℃");
                }
            } else {
                diagnosis.setDiagnosisResult(this.getDiagnosisResultByDiagnosisCriteria(diagnosisItem.getDiagnosisCriteriaList()));
            }
            diagnosisReport.put(diagnosisItem.getDiagnosisCode(), diagnosis);
            diagnosisItem.setDiagnosisResult(diagnosisReport.get(diagnosisItem.getDiagnosisCode()).getDiagnosisResult());
        }
    }

    /*
     * 엔진 제어 부문의 진단 여부 판정(CTR001)
     * P로 시작하는 DTC 코드가 1개이상 있는 경우 caution으로 판정한다.
     */
    private DiagnosisResult getDiagnosisResultByDtcList() {
        if(this.dtcRawDistinctList == null || this.dtcRawDistinctList.size() < 1) return DiagnosisResult.NORMAL;
        for(DtcRaw dtcRaw: this.dtcRawDistinctList) {
            if(dtcRaw.isCtr() && dtcRaw.isEngineCheck()) {
                return DiagnosisResult.FATAL;
            } else if(dtcRaw.isCtr() && dtcRaw.isStateCurrent()) {
                return DiagnosisResult.CAUTION;
            }
        }
        return DiagnosisResult.NORMAL;
    }
    private DiagnosisResult getDiagnosisResultOfTirePressure(DiagnosisItem diagnosisItem) {
        if(diagnosisItem.getDiagnosisCriteriaList().size() == 0) return DiagnosisResult.UNCERTAIN;
        DiagnosisResult diagnosisResult = this.getDiagnosisResultByDiagnosisCriteria(diagnosisItem.getDiagnosisCriteriaList());
        return diagnosisResult;
    }

    /* 진단 기준 별로 진단 여부 판정 */
    private DiagnosisResult getDiagnosisResultByDiagnosisCriteria(List<DiagnosisCriteria> diagnosisCriteriaList) {
        boolean isValidSpd = true;
        boolean isValid = true;
        boolean isFatal = true;
        boolean isCaution = true;
        // 진단 기준목록(diagnosisCriteriaList)이 없는 경우 정상으로 판정
        if(diagnosisCriteriaList.size() == 0) return DiagnosisResult.NORMAL;

        // 진단 기준 중 한가지라도 만족하지 않는 경우 valid, fatal, caution이 아닌것으로 판정한다.
        for(DiagnosisCriteria diagnosisCriteria: diagnosisCriteriaList) {
            if(!diagnosisCriteria.isSpdCntValid() && !diagnosisCriteria.isValid()) isValidSpd = false;
            if(!diagnosisCriteria.isValid()) isValid = false;
            if(!diagnosisCriteria.isFatal()) isFatal = false;
            if(!diagnosisCriteria.isCaution()) isCaution = false;
        }
        if(isValidSpd && !isValid) {
            return DiagnosisResult.NOT_AVAILABLE;
        } else if(!isValid) {
            return DiagnosisResult.UNCERTAIN;
        } else if(isFatal) {
            return DiagnosisResult.FATAL;
        } else if (isCaution) {
            return DiagnosisResult.CAUTION;
        } else {
            return DiagnosisResult.NORMAL;
        }
    }

    private boolean isOxySparse(int index, int cutoff) {
        if(sensingRawList.get(index).getCoolantTemp() == null || sensingRawList.get(index).getCoolantTemp() < validCoolantTemp) return false;
        for(int i = index - o2S2SparseTimeLag; i <= index + o2S2SparseTimeLag; i++) {
            if(i < 0 || i >= sensingRawList.size()) return false;
            if(sensingRawList.get(i) == null) return false;
            if(sensingRawList.get(i).getOxyVoltS2() == null) return false;
            if(sensingRawList.get(i).getOxyVoltS2() >= cutoff) return false;
        }
        return true;
    }
    /*
     * 분석대상 공회전 기준
     * 차량속도(vehicle_spd)가 0 으로 변한지 30초 이후인 경우,
     * 냉각수온(coolant_temp)이 70℃ 이상인 경우
     */
    private boolean isStableIdle(int index) {
        /* 냉각수온이 70미만인 경우 분석대상에서 제외 */
        if(sensingRawList.get(index).getCoolantTemp() == null || sensingRawList.get(index).getCoolantTemp() < validCoolantTemp) return false;

        /* 차량 공회전(ac off)이후 30초가 지나지 않은 경우 공회전 안정화 이전이므로 분석 대상에서 제외 */
        for(int i = 0; i < stableSecBefore; i++) {
            if(index - i < 0) return false;
            if(sensingRawList.get(index - i).getVehicleSpd() == null || sensingRawList.get(index - i).getVehicleSpd() > 0) return false;
        }
        /* 공회전 이후 가속하는 경우 데이터 수집의 delay를 감안하여 유효한 공회전이 아닌 것으로 판단한다. */
        for(int i = 0; i < stableSecAfter; i++) {
            if(index + i >= sensingRawList.size()) return false;
            if(sensingRawList.get(index + i).getVehicleSpd() == null || sensingRawList.get(index + i).getVehicleSpd() > 0) return false;
        }
        return true;
    }

    private boolean hasNull(Object... sensorValues) {
        for(Object sensorValue: sensorValues) {
            if(sensorValue == null) return true;
        }
        return false;
    }
}
