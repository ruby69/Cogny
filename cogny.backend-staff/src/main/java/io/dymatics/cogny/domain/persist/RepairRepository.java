package io.dymatics.cogny.domain.persist;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import io.dymatics.cogny.domain.model.Page;
import io.dymatics.cogny.domain.model.Repair;
import io.dymatics.cogny.domain.model.Repair.RepairComponent;

@Mapper
public interface RepairRepository {
    void insertRepair(Repair.Form form);

    void insertRepairComponentList(Repair.Form repairForm);
    
    void insertRepairComponent(RepairComponent repairComponent);

    Repair findByNo(Long repairNo);

    int countRepairListByPage(Page page);

    List<Repair> findRepairListByPage(Page page);

    int countRepairComponentListByPage(Page page);

    List<RepairComponent> findRepairComponentListByRepairNo(Long repairNo);

    List<RepairComponent> findRepairComponentListByPage(Page page);

    void updateRepair(Repair.Form form);

    void updateRepairComponent(RepairComponent repairComponent);

    void deleteRepair(Long repairNo);

    void deleteRepairComponentByRepairNo(Long repairNo);

    void deleteRepairComponent(Long repairComponentNo);
}