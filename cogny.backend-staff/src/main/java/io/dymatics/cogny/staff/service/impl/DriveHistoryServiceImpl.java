package io.dymatics.cogny.staff.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.dymatics.cogny.domain.model.DriveHistory;
import io.dymatics.cogny.domain.model.DriveHistory.DriveHistoryMemo;
import io.dymatics.cogny.domain.model.Page;
import io.dymatics.cogny.domain.persist.DriveHistoryRepository;
import io.dymatics.cogny.domain.persist.DtcRepository;
import io.dymatics.cogny.staff.service.DriveHistoryService;

@Service
@Transactional(readOnly = true)
public class DriveHistoryServiceImpl implements DriveHistoryService{
    @Autowired private DriveHistoryRepository driveHistoryRepository;
    @Autowired private DtcRepository dtcRepository;

    @Override
    public Object getStartDates(Long vehicleNo) {
        return driveHistoryRepository.findStartDates(vehicleNo);
    }

    @Override
    public Object getDriveHistoryIndexes(DriveHistory driveHistory) {
        return driveHistoryRepository.findDriveHistoryIndexes(driveHistory);
    }

    @Override
    public Object getDriveHisotyByNo(Long driveHistoryNo) {
        return driveHistoryRepository.findByNo(driveHistoryNo);
    }

    @Override
    public Object populateDriveHistory(Page page) {
        page.setTotal(driveHistoryRepository.countByPage(page));
        List<DriveHistory> driveHistoryList = driveHistoryRepository.findByPage(page);
        for(DriveHistory driveHistory: driveHistoryList) {
            driveHistory.setDtcRawList(dtcRepository.findByDriveHistoryNo(driveHistory.getDriveHistoryNo()));
        }
        page.setContents(driveHistoryList);
        return page;
    }
    
    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public Object saveMemo(DriveHistoryMemo driveHistoryMemo) {
        if(driveHistoryMemo.getDriveHistoryMemoNo() != null) {
            driveHistoryRepository.updateMemo(driveHistoryMemo);
        } else {
            driveHistoryRepository.insertMemo(driveHistoryMemo);
        }
        return driveHistoryRepository.findMemoByNo(driveHistoryMemo.getDriveHistoryMemoNo());
    }
}
