package io.dymatics.cogny.jobs.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import io.dymatics.cogny.domain.model.Diagnosis.DiagnosisDriveHistory;
import io.dymatics.cogny.domain.model.Diagnosis.DiagnosisHistory;
import io.dymatics.cogny.domain.model.Diagnosis.DiagnosisHistory.DiagnosisType;
import io.dymatics.cogny.domain.model.DriveHistory;
import io.dymatics.cogny.domain.persist.DiagnosisRepository;
import io.dymatics.cogny.domain.persist.DriveHistoryRepository;
import io.dymatics.cogny.jobs.service.DiagnosisExecuteService;
import io.dymatics.cogny.jobs.service.DiagnosisService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DiagnosisExecuteServiceImpl  implements DiagnosisExecuteService{
    @Autowired private DriveHistoryRepository driveHistoryRepository;
    @Autowired private DiagnosisRepository diagnosisRepository;
    @Autowired private DiagnosisService diagnosisService;
    @Value("#{taskExecutor}") private ThreadPoolTaskExecutor taskExecutor;

    // 최근 x분 이내에 분석한 운행 기록은 분석하지 않는다.
    @Value("${batch.diagnosis.diagnosis-interval-sec}") private int diagnosisIntervalSec;
    
    @Override
    public void diagnosisBatchByDriveHistoryNo(Long driveHistoryNo) throws Exception {
        DiagnosisDriveHistory diagnosisDriveHistory = new DiagnosisDriveHistory();
        diagnosisDriveHistory.setDriveHistoryNo(driveHistoryNo);
        diagnosisRepository.insertDiagnosisDriveHistory(diagnosisDriveHistory);
        diagnosisDriveHistory.setDriveHistory(driveHistoryRepository.findByNo(driveHistoryNo));

        // 새로 진행하는 진단 이력(diagnosis_history)을 생성하고 DB에 입력한다.
        DiagnosisHistory currentDiagnosisHistory = new DiagnosisHistory();
        currentDiagnosisHistory.setDiagnosisType(DiagnosisType.MANUAL);
        diagnosisRepository.insertDiagnosisHistory(currentDiagnosisHistory);
        currentDiagnosisHistory = diagnosisRepository.findDiagnosisHistoryByNo(currentDiagnosisHistory.getDiagnosisHistoryNo());
        
        this.dianosisForDiagnosisDriveHistory(diagnosisDriveHistory, currentDiagnosisHistory);
        
        currentDiagnosisHistory.setDriveHistoryCnt(1l);
        diagnosisRepository.finishDiagnosisHistory(currentDiagnosisHistory);
    }
    
    @Override
    public void diagnosisBatch() throws Exception {
        // 최종 진단 시점을 조회하여 진단 대상이 되는 drive_history를 가져와서 마지막 진단 이후에 생성된 drive_history를 가져오거나 없는 경우 신규 instance를 생성하여 할당한다.
        DiagnosisHistory lastDiagnosisHistory = diagnosisRepository.findLastDiagnosisHistory();
        if(lastDiagnosisHistory != null) {
            diagnosisRepository.updateRecentDiagnosisDate(lastDiagnosisHistory.getDiagnosisHistoryNo());
            lastDiagnosisHistory = diagnosisRepository.findDiagnosisHistoryByNo(lastDiagnosisHistory.getDiagnosisHistoryNo());
        } else {
            // 최초 실행하여 DB 레코드가 없는 경우 한번 실행하는 코드
            lastDiagnosisHistory = new DiagnosisHistory();
            lastDiagnosisHistory.setRecentDiagnosisDate(new Date());
            log.info("recent time : " + lastDiagnosisHistory.getRecentDiagnosisDate());
        }
        List<DriveHistory> driveHistoryList = driveHistoryRepository.findDriveHistoryByDiagnosisHistory(lastDiagnosisHistory);

//        // 새로 입력된 drive_history를 조회하여 분석 대상 테이블에 입력한다.
        for(DriveHistory driveHistory: driveHistoryList) {
            DiagnosisDriveHistory diagnosisDriveHistory = new DiagnosisDriveHistory();
            diagnosisDriveHistory.setDriveHistoryNo(driveHistory.getDriveHistoryNo());
            diagnosisRepository.insertDiagnosisDriveHistory(diagnosisDriveHistory);
        }

        DiagnosisDriveHistory diagnosisDriveHistory = new DiagnosisDriveHistory();
        // 최근 분석한 기록이 있는 drive_history는 분석하지 않는다
        diagnosisDriveHistory.setDiagnosisIntervalSec(diagnosisIntervalSec);
        diagnosisDriveHistory.setCurrentDiagnosisTime(lastDiagnosisHistory.getRecentDiagnosisDate());

        // 분석 대상 drive_history 조회
        List<DiagnosisDriveHistory> diagnosisDriveHistoryList = diagnosisRepository.findDiagnosisDriveHistory(diagnosisDriveHistory);
        
        // 분석 대상 drive_history가 없는 경우 프로세스를 종료한다.
        if(diagnosisDriveHistoryList.size() < 1) return;

        // 새로 진행하는 진단 이력(diagnosis_history)을 생성하고 DB에 입력한다.
        DiagnosisHistory currentDiagnosisHistory = new DiagnosisHistory();
        currentDiagnosisHistory.setDiagnosisType(DiagnosisType.BATCH);
        diagnosisRepository.insertDiagnosisHistory(currentDiagnosisHistory);
        currentDiagnosisHistory = diagnosisRepository.findDiagnosisHistoryByNo(currentDiagnosisHistory.getDiagnosisHistoryNo());
        

        for(DiagnosisDriveHistory targetDiagnosisDriveHistory: diagnosisDriveHistoryList) {
            this.dianosisForDiagnosisDriveHistory(targetDiagnosisDriveHistory, currentDiagnosisHistory);
        }

        // 진단 이력 테이블의 종료 시각 기록
        currentDiagnosisHistory.setDriveHistoryCnt(Long.valueOf(diagnosisDriveHistoryList.size()));
        diagnosisRepository.finishDiagnosisHistory(currentDiagnosisHistory);
    }
    
    private void dianosisForDiagnosisDriveHistory(DiagnosisDriveHistory targetDiagnosisDriveHistory, DiagnosisHistory currentDiagnosisHistory) {
        try {
            targetDiagnosisDriveHistory.setLastDiagnosisHistoryNo(currentDiagnosisHistory.getDiagnosisHistoryNo());
            diagnosisService.analyzeSingleDrive(targetDiagnosisDriveHistory, currentDiagnosisHistory);
        } catch(Exception e) {
            // 진단이 실패한 경우 진단 성공 일시를 null로 변환한다.(cf 진단 성공시에는 diagnosisService.analyzeSingleDrive에서 날짜를 입력한다.
            targetDiagnosisDriveHistory.setSucceeded(false);
            diagnosisRepository.updateDiagnosisDriveHistory(targetDiagnosisDriveHistory);
            log.error("errors on diagnosis process, targetDiagnosisDriveHistory : " + targetDiagnosisDriveHistory.toString() + "\n currentDiagnosisHistory : " + currentDiagnosisHistory.toString());
            log.error("Exception : " + e.toString(), e);
            return;
        }
    }
}
