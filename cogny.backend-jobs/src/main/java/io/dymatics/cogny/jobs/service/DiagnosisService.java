package io.dymatics.cogny.jobs.service;

import io.dymatics.cogny.domain.model.Diagnosis.DiagnosisDriveHistory;
import io.dymatics.cogny.domain.model.Diagnosis.DiagnosisHistory;

public interface DiagnosisService {

    void analyzeSingleDrive(DiagnosisDriveHistory targetDiagnosisDriveHistory, DiagnosisHistory currentDiagnosisHistory) throws Exception;


}
