package io.dymatics.cogny.jobs.service;

public interface DiagnosisExecuteService {

    void diagnosisBatchByDriveHistoryNo(Long driveHistoryNo) throws Exception;

    void diagnosisBatch() throws Exception;

    
}
