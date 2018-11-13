package io.dymatics.cogny.api.service;

import java.util.List;

import io.dymatics.cogny.domain.model.DriveHistory;
import io.dymatics.cogny.domain.model.DriveRepairLog;
import io.dymatics.cogny.domain.model.DtcRaw;
import io.dymatics.cogny.domain.model.SensingLog;
import io.dymatics.cogny.domain.model.Sensings;

public interface DriveService {

    Object saveStart(DriveHistory driveHistory);

    void saveEnd(DriveHistory driveHistory);

    void saveSensings(Sensings sensings);

    Object repairMessage(Long vehicleNo);

    DriveRepairLog.Page populateHistories(DriveRepairLog.Page page);



    //////////////////////////////////////////////////////////////////////////////////////////////////

    @Deprecated
    void saveSensingRawOld(List<SensingLog> sensingLogs);

    @Deprecated
    void saveDtcRawsOld(List<DtcRaw> dtcRaws);

}
