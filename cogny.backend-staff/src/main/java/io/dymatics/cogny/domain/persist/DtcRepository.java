package io.dymatics.cogny.domain.persist;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import io.dymatics.cogny.domain.model.DtcHistory;
import io.dymatics.cogny.domain.model.DtcRaw;

@Mapper
public interface DtcRepository {
    List<DtcRaw> findByDriveHistoryNo(Long driveHistoryNo);
    
    void insertDtcHistory(DtcHistory dtcHistory);
    
    void updateDtcHistory(Long dtcHistoryNo);
    
    DtcHistory findByVehicleDtcCode(DtcRaw dtcRaw);
    
    
//    List<DtcRaw> findDistinctDtc(Long driveHistoryNo);
}
