package io.dymatics.cogny.staff.service;

import java.util.List;

import io.dymatics.cogny.domain.model.SensingRaw;

public interface SensingRawService {

    Object getSensingRaw(SensingRaw.Form form) throws Exception;

    List<SensingRaw> getSensingRawDatastore(SensingRaw.Form form) throws Exception;
    
    void migrationSensingRaw(Long startDriveHistoryNo, Long endDriveHistoryNo);
}
