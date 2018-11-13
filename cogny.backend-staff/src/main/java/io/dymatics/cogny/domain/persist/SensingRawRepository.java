package io.dymatics.cogny.domain.persist;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import io.dymatics.cogny.domain.model.DriveHistory;
import io.dymatics.cogny.domain.model.SensingRaw;

@Mapper
public interface SensingRawRepository {
    
    int countByPage(SensingRaw.Form form);
    
    List<SensingRaw> findByPage(SensingRaw.Form form);
    
    List<SensingRaw> findAllByDriveHistory(DriveHistory driveHistory);
}
