package io.dymatics.cogny.domain.persist;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import io.dymatics.cogny.domain.model.Diagnosis;
import io.dymatics.cogny.domain.model.Diagnosis.DiagnosisDriveHistory;
import io.dymatics.cogny.domain.model.Diagnosis.DiagnosisHistory;
import io.dymatics.cogny.domain.model.DiagnosisAnalyzer;
import io.dymatics.cogny.domain.model.DiagnosisCriteria;
import io.dymatics.cogny.domain.model.DiagnosisItem;

@Mapper
public interface DiagnosisRepository {
    /* 진단 (diagnosis) 테이블 */
    void insertDiagnosis(Diagnosis diagnosis);
    
    Diagnosis findPrevDiagnosis(Diagnosis diagnosis);
    
    void updateDiagnosisResult(Diagnosis diagnosis);

    /* 진단 항목 테이블(diagnosis_item) */
    List<DiagnosisItem> findDiagnosisItemByDriveHistoryNo(Long driveHistoryNo);

    /* 진단 이력 테이블(diagnosis_history) 처리 */
    void insertDiagnosisHistory(DiagnosisHistory diagnosisHistory);

    DiagnosisHistory findLastDiagnosisHistory();
    
    DiagnosisHistory findDiagnosisHistoryByNo(Long diagnosisHistoryNo);
    
    void updateRecentDiagnosisDate(Long diagnosisHistoryNo);

    void finishDiagnosisHistory(DiagnosisHistory diagnosisHistory);

    /* 운행 기록별 진단이력 관리 테이블(diagnosis_drive_history) */
    void insertDiagnosisDriveHistory(DiagnosisDriveHistory diagnosisDriveHistory);

    List<DiagnosisDriveHistory> findDiagnosisDriveHistory(DiagnosisDriveHistory diagnosisDriveHistory);

    void updateDiagnosisDriveHistory(DiagnosisDriveHistory diagnosisDriveHistory);

    /* 진단 기준(diagnosis_criteria) 테이블 */
    List<DiagnosisCriteria> findDiagnosisCriteriaByItemNo(Long diagnosisItemNo);

    /* 진단 이력 로그(insertDiagnosisLog)을 기록한다. */
    void insertDiagnosisLog(DiagnosisAnalyzer diagnosisAnalyzer);

}
