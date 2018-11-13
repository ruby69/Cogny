package io.dymatics.cogny.jobs.service;

import java.util.List;

import io.dymatics.cogny.domain.model.DriveHistory;
import io.dymatics.cogny.domain.model.SensingRaw;

public interface SensingRawService {
    List<SensingRaw> getSensingRawDatastore(DriveHistory driveHistory) throws Exception;
}
