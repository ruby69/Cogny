package io.dymatics.cogny.jobs.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import io.dymatics.cogny.domain.model.Diagnosis;
import io.dymatics.cogny.domain.model.Diagnosis.DiagnosisDriveHistory;
import io.dymatics.cogny.domain.model.Diagnosis.DiagnosisHistory;
import io.dymatics.cogny.domain.model.Diagnosis.DiagnosisResult;
import io.dymatics.cogny.domain.model.DiagnosisAnalyzer;
import io.dymatics.cogny.domain.model.DiagnosisItem;
import io.dymatics.cogny.domain.model.DriveHistory;
import io.dymatics.cogny.domain.model.DriveHistory.DriveRepairLog;
import io.dymatics.cogny.domain.model.DtcHistory;
import io.dymatics.cogny.domain.model.DtcRaw;
import io.dymatics.cogny.domain.model.MobileDevice;
import io.dymatics.cogny.domain.model.MobileDevice.UserMobileDevice;
import io.dymatics.cogny.domain.model.PerformHistory;
import io.dymatics.cogny.domain.model.SensingRaw;
import io.dymatics.cogny.domain.model.Vehicle;
import io.dymatics.cogny.domain.persist.DiagnosisRepository;
import io.dymatics.cogny.domain.persist.DriveHistoryRepository;
import io.dymatics.cogny.domain.persist.DtcRepository;
import io.dymatics.cogny.domain.persist.PerformHistoryRepository;
import io.dymatics.cogny.domain.persist.SensingRawRepository;
import io.dymatics.cogny.domain.persist.UserMobileDeviceRepository;
import io.dymatics.cogny.domain.persist.VehicleRepository;
import io.dymatics.cogny.jobs.service.DiagnosisService;
import io.dymatics.cogny.jobs.service.PushService;
import io.dymatics.cogny.jobs.service.SensingRawService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional(readOnly = true)
public class DiagnosisServiceImpl implements DiagnosisService {
    @Autowired private DiagnosisRepository diagnosisRepository;
    @Autowired private DtcRepository dtcRepository;
    @Autowired private SensingRawRepository sensingRawRepository;
    @Autowired private PerformHistoryRepository performHistoryRepository;
    @Autowired private DriveHistoryRepository driveHistoryRepository;
    @Autowired private UserMobileDeviceRepository userMobileDeviceRepository;
    @Autowired private VehicleRepository vehicleRepository;
    
    @Autowired private SensingRawService sensingRawService;
    @Autowired private PushService pushService;
    
    // 마지막 센서 데이터 입력 후 x일이 지난 경우 운행종료 처리(TODO 사용하지 않음)
//    @Value("${batch.diagnosis.expire-drive-day}") private int expireDriveDay;
    // 마지막 센서 데이터 입력 후 x일이 지난 경우 운행종료 처리
    @Value("${batch.diagnosis.expire-drive-min}") private int expireDriveMin;
    // 산소센서S2 희박 조건을 위한 time lag x s
    @Value("${batch.diagnosis.o2-s2-sparse-time-lag-sec}") private int o2S2SparseTimeLag;
    // 공회전 또는 AC OFF 이후 안정화 기준 시간 x s
    @Value("${batch.diagnosis.stable-idle-sec-before}") private int stableSecBefore;
    // 공회전이 종료되고 속도가 0 이상이 된 시점이 x초 이후인 공회전만 유효한 공회전으로 판단한다.
    @Value("${batch.diagnosis.stable-idle-sec-after}") private int stableSecAfter;
    // 냉각수 온도가 x ℃ 이후의 데이터만 피드백 제어를 시작하는 것으로 판단
    @Value("${batch.diagnosis.valid-coolant-temp}") private int validCoolantTemp;

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void analyzeSingleDrive(DiagnosisDriveHistory targetDiagnosisDriveHistory, DiagnosisHistory currentDiagnosisHistory) throws Exception {
        DriveHistory driveHistory = driveHistoryRepository.findByNo(targetDiagnosisDriveHistory.getDriveHistoryNo());
        
        // drive_history 별로 진단 항목 목록을 조회한다.
        List<DiagnosisItem> diagnosisItemList = diagnosisRepository.findDiagnosisItemByDriveHistoryNo(targetDiagnosisDriveHistory.getDriveHistoryNo());
        // drive_history별로 dtc를 조회한다.
        List<DtcRaw> dtcRawList = dtcRepository.findByDriveHistoryNo(targetDiagnosisDriveHistory.getDriveHistoryNo());
        List<DtcRaw> dtcRawDistinctList = this.getDtcRawDistinctList(dtcRawList);
        //  sensingRaw 조회
        List<SensingRaw> sensingRawList = sensingRawService.getSensingRawDatastore(targetDiagnosisDriveHistory.getDriveHistory());
        // datastore에 sensing_raw 데이터가 없는 경우 mysql에서 조회한다.
        if(sensingRawList.size() == 0) {
            log.info("sensing_raw from mysql, drive_history_no : " + targetDiagnosisDriveHistory.getDriveHistoryNo());
            sensingRawList = sensingRawRepository.findAllByDriveHistory(targetDiagnosisDriveHistory.getDriveHistory());
        }
        // sensor 데이터가 없고 운행 이력(drive_history) 생성 이후 2일이 지난 경우 진단 종료 처리하고 다음 분석에서 배제한다.
        if(sensingRawList.size() == 0) {
            if(this.isFinalDiagnosis(driveHistory, currentDiagnosisHistory.getStartDate())) {
                // diagnosis_drive_history 테이블에 진단 이력 번호 기록 및 진단 완료 처리
                targetDiagnosisDriveHistory.setFinished(true);
                diagnosisRepository.updateDiagnosisDriveHistory(targetDiagnosisDriveHistory);
            }
            return;
        }

        // 차량 진단을 수행한다.
        DiagnosisAnalyzer diagnosisAnalyzer = new DiagnosisAnalyzer(o2S2SparseTimeLag, stableSecBefore, stableSecAfter, validCoolantTemp);
        diagnosisAnalyzer.execute(driveHistory, sensingRawList, diagnosisItemList, dtcRawDistinctList);
        // 진단 항목별 진단 결과를 조회하고 새로 발생한 경우 DB 입력
        this.writeDiagnosis(diagnosisAnalyzer, diagnosisItemList, currentDiagnosisHistory.getDiagnosisHistoryNo());
        
        // dtc_history 및 dtc 관련 perform_history를 입력한다.
        this.writeDtcHistory(dtcRawDistinctList);
        
        // 운전자용 운행이력 테이블(drive_repair_log)을 기록한다.
        this.writeDriveRepairLog(driveHistory, sensingRawList);
        
        // 분석한 drive_history의 운행이 종료된 경우 또는 마지막 sensing_raw 입력 이후 2일이상 지난경우 diagnosis.final_diagnosis_date 칼럽을 현재 시간으로 기록한다.
        if(targetDiagnosisDriveHistory.getDriveHistory().getEndTime() != null || this.hasFinalizeWithSensingRaw(sensingRawList, currentDiagnosisHistory.getStartDate())) {
            targetDiagnosisDriveHistory.setFinished(true);
            diagnosisAnalyzer.setFinishedDrive(true);
            
            // 마지막 sensingRaw 입력 후 x분이 지나면 drive_history를 운행완료 처리 한다.
            if(targetDiagnosisDriveHistory.getDriveHistory().getEndTime() == null
                    && this.hasFinalizeWithSensingRaw(sensingRawList, currentDiagnosisHistory.getStartDate())
                    && sensingRawList != null
                    && sensingRawList.size() > 0) {
                
                Date endTime = sensingRawList.get(sensingRawList.size() - 1).getSensingTime();
                Long endMileage = 0l + sensingRawList.get(sensingRawList.size() - 1).getOdometer();
                Long driveDistance = 0l + sensingRawList.get(sensingRawList.size() - 1).getOdometer() - sensingRawList.get(0).getOdometer();
                
                driveHistory.setEndTime(endTime);
                driveHistory.setEndMileage(endMileage);
                driveHistory.setDriveDistance(driveDistance);
                driveHistoryRepository.finishDriveHistory(driveHistory);
            }
        }
        // 진단이 완료된 항목에 대해 diagnosis_drive_history의 last_diagnosis_history_no와 drive_history_no를 갱신하고 진단 성공으로 기록한다.
        targetDiagnosisDriveHistory.setSucceeded(true);
        // diagnosis_drive_history 테이블에 진단 이력 번호 기록
        diagnosisRepository.updateDiagnosisDriveHistory(targetDiagnosisDriveHistory);
        // 진단 결과 로그 임력을 위해 diagnosisHistoryNo를 설정한다.
        diagnosisAnalyzer.setDiagnosisHistoryNo(currentDiagnosisHistory.getDiagnosisHistoryNo());
        // 진단 항목별 진단결과가 있는 경우 로그를 기록한다.
        if(diagnosisAnalyzer.getDiagnosisItemList().size() > 0) {
            diagnosisRepository.insertDiagnosisLog(diagnosisAnalyzer);
        }
        log.info("Finish Batch (drive_history_no : " + targetDiagnosisDriveHistory.getDriveHistoryNo() + ", hasFinished : " + targetDiagnosisDriveHistory.isFinished() + ")");
    }
    
    private List<DtcRaw> getDtcRawDistinctList(List<DtcRaw> dtcRawList) {
        List<DtcRaw> dtcRawDistinctList = new ArrayList<DtcRaw>();
        DtcRaw prevDtcRaw = new DtcRaw();
//        int i = 0;
        for(DtcRaw dtcRaw: dtcRawList) {
            // dtc state 현재인 경우가 처음 등장한 경우
            if(dtcRaw.isEngineCheck()) {
                if(StringUtils.isEmpty(prevDtcRaw.getDtcCode()) 
                        || !prevDtcRaw.getDtcCode().equals(dtcRaw.getDtcCode())) {
                    prevDtcRaw = dtcRaw;
                    dtcRawDistinctList.add(dtcRaw);
                } else if (!prevDtcRaw.isEngineCheck()) {
                    dtcRawDistinctList.remove(dtcRawDistinctList.size() - 1);
                    prevDtcRaw = dtcRaw;
                    dtcRawDistinctList.add(dtcRaw);
                }
            } else if(StringUtils.isEmpty(prevDtcRaw.getDtcCode()) 
                    || !dtcRaw.getDtcCode().equals(prevDtcRaw.getDtcCode())) {
                if(dtcRaw.isStateCurrent()) {
                    prevDtcRaw = dtcRaw;
                    dtcRawDistinctList.add(dtcRaw);
                }
            }
        }
        return dtcRawDistinctList;
    }

    private void writeDiagnosis(DiagnosisAnalyzer diagnosisAnalyzer, List<DiagnosisItem> diagnosisItemList, long currentDiagnosisHistoryNo) {
        for(DiagnosisItem diagnosisItem : diagnosisItemList) {
            Diagnosis diagnosis = diagnosisAnalyzer.getDiagnosisReport(diagnosisItem.getDiagnosisCode());
            // 해당코드의 점검 결과가 없는 경우
            if(diagnosis == null) continue;
            // 기존 진단 내역 조회
            Diagnosis prevDiagnosis = diagnosisRepository.findPrevDiagnosis(diagnosis);
            // 진단 내역 입력
            diagnosis.setDiagnosisHistoryNo(currentDiagnosisHistoryNo);
            // 기존 진단 결과가 없고, 진단결과 '정상' 또는 '불확실' 이 아닌 '주의', '경고', '지원하지 않음'인 경우 diagnosis를 insert 한다.
            if(this.isNewDiagnosis(prevDiagnosis, diagnosis)) {
                diagnosisRepository.insertDiagnosis(diagnosis);
                // fatal인경우 push를 발송한다.
                if(diagnosis.getDiagnosisResult() == DiagnosisResult.FATAL) {
                    this.pushFatalAlert(diagnosisAnalyzer.getVehicleNo());
                }
            } else if(this.isChangedDiagnosis(prevDiagnosis, diagnosis)) {
                diagnosis.setDiagnosisNo(prevDiagnosis.getDiagnosisNo());
                diagnosisRepository.updateDiagnosisResult(diagnosis);
            }
            // tire가 NORMAL인 경우 별도로 타이어압력 입력
            if(diagnosisItem.isTir() && diagnosis.getDiagnosisResult() == DiagnosisResult.NORMAL) {
                if(prevDiagnosis == null) {
                    diagnosisRepository.insertDiagnosis(diagnosis);
                } else if(prevDiagnosis != null 
                        && prevDiagnosis.getDiagnosisResult() == DiagnosisResult.NORMAL){
                    diagnosis.setDiagnosisNo(prevDiagnosis.getDiagnosisNo());
                    diagnosisRepository.updateDiagnosisResult(diagnosis);
                }
            }
            
            // performHistory를 입력한다.
            /* 
             * 1) repair_no가 null 기존 진단결과가 없을 떄 진단 결과가 '주의' 또는 '경고'인 경우 진단 이력 화면 조회용 데이터(perform_history)를 입력한다.
             * 2) repair_no가 null인 기존 진단 결과있을 때 '주의'에서 '경고'로 바뀐 경우 새로 입력한다.
             */
            Long vehicleNo = diagnosis.getVehicleNo();
            String title = diagnosisItem.getName() + " " + diagnosis.getDiagnosisResultName();
            String body = null;
            Long refNo = diagnosis.getDiagnosisNo();
            Date issuedTime = new Date();
            if((prevDiagnosis == null
                    && (diagnosis.getDiagnosisResult() == DiagnosisResult.CAUTION
                        || diagnosis.getDiagnosisResult() == DiagnosisResult.FATAL)
                ) || (prevDiagnosis != null
                      && prevDiagnosis.getDiagnosisResult() == DiagnosisResult.CAUTION
                      && diagnosis.getDiagnosisResult() == DiagnosisResult.FATAL)
                     ) {
                // 엔진 제어가 아닌 경우만 처리한다. 엔진제어(dtc_history)는 이후 일괄 처리한다.
                if(!diagnosisItem.isCtr()) {
                    performHistoryRepository.insert(PerformHistory.diagnosis(vehicleNo, title, body, refNo, issuedTime));
                }
            }
        }
    }
    
    private void writeDtcHistory(List<DtcRaw> dtcRawDistinctList) {
        // dtc_history 및 perform_history 입력
        for(DtcRaw dtcRaw: dtcRawDistinctList) {
            DtcHistory dtcHistory = dtcRepository.findByVehicleDtcCode(dtcRaw);
            if(dtcHistory == null) {
                // dtc_history 입력
                dtcHistory = new DtcHistory();
                dtcHistory.setVehicleNo(dtcRaw.getVehicleNo());
                dtcHistory.setObdDeviceNo(dtcRaw.getObdDeviceNo());
                dtcHistory.setDriveHistoryNo(dtcRaw.getDriveHistoryNo());
                dtcHistory.setDtcIssuedTime(dtcRaw.getDtcIssuedTime());
                dtcHistory.setDtcCode(dtcRaw.getDtcCode());
                dtcRepository.insertDtcHistory(dtcHistory);

                // performHistory 입력
                Long vehicleNo = dtcRaw.getVehicleNo();
                String title = dtcRaw.getDtcCode();
                String body = dtcRaw.getDesc();
                Long refNo = dtcHistory.getDtcHistoryNo();
                Date issuedTime = dtcRaw.getDtcIssuedTime();
                performHistoryRepository.insert(PerformHistory.dtcHistory(vehicleNo, title, body, refNo, issuedTime));

            } else {
                dtcRepository.updateDtcHistory(dtcHistory.getDtcHistoryNo());
            }
        }
    }
    
    private void writeDriveRepairLog(DriveHistory driveHistory, List<SensingRaw> sensingRawList) {
        if(this.hasNull(sensingRawList.get(sensingRawList.size() -1).getSensingTime(), 
                        sensingRawList.get(0).getSensingTime(),
                        sensingRawList.get(sensingRawList.size() - 1).getOdometer(),
                        sensingRawList.get(0).getOdometer()
                )) {
            log.error("invalid values in the 'sensingRaw', drive_history_no : " +driveHistory.getDriveHistoryNo());
            return;
        }
        Long driveTime = (sensingRawList.get(sensingRawList.size() -1).getSensingTime().getTime() - sensingRawList.get(0).getSensingTime().getTime()) / 1000;
        Integer driveMileage = sensingRawList.get(sensingRawList.size() - 1).getOdometer() - sensingRawList.get(0).getOdometer();
        Integer totalMileage = sensingRawList.get(sensingRawList.size() - 1).getOdometer();

        DriveRepairLog driveRepairLog = driveHistoryRepository.findDriveRepairLogByDriveHistoryNo(driveHistory.getDriveHistoryNo());
        if(driveRepairLog == null) {
            driveRepairLog = new DriveRepairLog();
            driveRepairLog.setVehicleNo(driveHistory.getVehicleNo());
            driveRepairLog.setUserNo(driveHistory.getUserNo());
            driveRepairLog.setDriveTime(driveTime);
            driveRepairLog.setDriveMileage(Long.valueOf(driveMileage));
            driveRepairLog.setTotalMileage(Long.valueOf(totalMileage));
            driveRepairLog.setRefNo(driveHistory.getDriveHistoryNo());
            driveRepairLog.setStartDate(driveHistory.getStartDate());
            driveHistoryRepository.insertDriveRepairLogForDrive(driveRepairLog);
        } else {
            driveRepairLog.setDriveTime(driveTime);
            driveRepairLog.setDriveMileage(Long.valueOf(driveMileage));
            driveRepairLog.setTotalMileage(Long.valueOf(totalMileage));
            driveHistoryRepository.updateDriveRepairLogForDrive(driveRepairLog);
        }
    }
//    private void pushFatalAlert(Long vehicleNo, List<DiagnosisItem> diagnosisItemList) {
    private void pushFatalAlert(Long vehicleNo) {
//        boolean hasFatal = false;
//        for(DiagnosisItem diagnosisItem: diagnosisItemList) {
//            if(diagnosisItem.getDiagnosisResult() == DiagnosisResult.FATAL) {
//                hasFatal = true;
//                break;
//            }
//        }
//        if(hasFatal) {
            Vehicle vehicle = vehicleRepository.findByNo(vehicleNo);
            List<MobileDevice.UserMobileDevice> userMobileDeviceList = userMobileDeviceRepository.findByVehicleNo(vehicleNo);
            String pushMsg = vehicle.getLicenseNo() + "의 긴급 점검이 필요합니다.";
            for(UserMobileDevice userMobileDevice: userMobileDeviceList) {
                pushService.noti(userMobileDevice.getUserMobileDeviceNo(), pushMsg);
//            }
        }
    }
    private boolean isFinalDiagnosis(DriveHistory driveHistory, Date currentDiagnosisTime) {
        if(driveHistory == null) return true;
        if(driveHistory.getEndTime() != null ) return true;
        if(StringUtils.isEmpty(driveHistory.getStartDate()) || StringUtils.isEmpty(driveHistory.getStartTime())) return true;
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
        try {
            // 운행이력 기록 후 x일이 지난경우 종료 처리 한다.
            Date driveStartDate = f.parse(driveHistory.getStartDate() + " " + driveHistory.getStartTime());
            long timeDiff = currentDiagnosisTime.getTime() - driveStartDate.getTime();
            if(timeDiff > this.expireDriveMin * 60 * 1000) return true;
//            if(timeDiff > this.expireDriveDay * 24 * 60 * 60 * 1000) return true;
        } catch (ParseException e) {
            log.error("parse exception(drive_history_no) : " + driveHistory.getDriveHistoryNo() + e.toString());
            return true;
        }
        return false;
    }
    
    private boolean hasFinalizeWithSensingRaw(List<SensingRaw> sensingRawList, Date currentDiagnosisTime) {
        if(sensingRawList == null || sensingRawList.size() == 0) return true;
        if(sensingRawList.get(sensingRawList.size() - 1).getSensingTime() == null) return true;
        // 마지막 센서 데이터 입력 후 2일이 지난 경우
        long timeDiff = currentDiagnosisTime.getTime() - sensingRawList.get(sensingRawList.size() - 1).getSensingTime().getTime();
        if(timeDiff > this.expireDriveMin * 60 * 1000) return true;
//        if(timeDiff > this.expireDriveDay * 24 * 60 * 60 * 1000) return true;

        return false;
    }
    
    private boolean isNewDiagnosis(Diagnosis prevDiagnosis, Diagnosis diagnosis) {
        if(prevDiagnosis == null
                && diagnosis.getDiagnosisResult() != DiagnosisResult.NORMAL
                && diagnosis.getDiagnosisResult() != DiagnosisResult.UNCERTAIN) {
            return true;
        }
        return false;
    }
    
    private boolean isChangedDiagnosis(Diagnosis prevDiagnosis, Diagnosis diagnosis) {
        if(prevDiagnosis != null
                    && diagnosis.getDiagnosisResult() != prevDiagnosis.getDiagnosisResult()
                    && diagnosis.getDiagnosisResult() != DiagnosisResult.NORMAL
                    && diagnosis.getDiagnosisResult() != DiagnosisResult.UNCERTAIN) {
            return true;
        }
        return false;
    }
    
    private boolean hasNull(Object... nullableValues) {
        for(Object nullableValue: nullableValues) {
            if(nullableValue == null) return true;
        }
        return false;
    }
}
