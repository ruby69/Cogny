package io.dymatics.cogny.staff.service;

import io.dymatics.cogny.domain.model.DriveHistory;
import io.dymatics.cogny.domain.model.DriveHistory.DriveHistoryMemo;
import io.dymatics.cogny.domain.model.Page;

public interface DriveHistoryService {
    
    Object getStartDates(Long vehicleNo);

    Object getDriveHistoryIndexes(DriveHistory driveHistory);

    Object getDriveHisotyByNo(Long driveHistoryNo);

    Object populateDriveHistory(Page page);

    Object saveMemo(DriveHistoryMemo driveHistoryMemo);

}
