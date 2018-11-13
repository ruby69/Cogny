package io.dymatics.cogny.domain.persist;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import io.dymatics.cogny.domain.model.DriveHistory;
import io.dymatics.cogny.domain.model.DriveHistory.DriveHistoryMemo;
import io.dymatics.cogny.domain.model.DriveHistory.DriveRepairLog;
import io.dymatics.cogny.domain.model.Page;

@Mapper
public interface DriveHistoryRepository {

    List<DriveHistory> findStartDates(Long vehicleNo);

    List<DriveHistory> findDriveHistoryIndexes(DriveHistory driveHistory);
    
    DriveHistory findByNo(Long driveHistoryNo);

    int countByPage(Page page);

    List<DriveHistory> findByPage(Page page);
    
    List<DriveHistory> findAll(@Param("startDriveHistoryNo") Long startDriveHistoryNo, @Param("endDriveHistoryNo") Long endDriveHistoryNo);

    //운행이력 메모(drive_history_memo) 테이블
    void insertMemo(DriveHistoryMemo driveHistoryMemo);

    void updateMemo(DriveHistoryMemo driveHistoryMemo);

    DriveHistoryMemo findMemoByNo(Long driveHistoryMemoNo);
    
    // 운전자 안내 메시지(drive_repair_log) 테이블
    void insertDriveRepairLogForDrive(DriveRepairLog driveRepairLog);

    DriveRepairLog findDriveRepairLogByDriveHistoryNo(Long driveHistoryNo);

    void updateDriveRepairLogForDrive(DriveRepairLog driveRepairLog);
}
