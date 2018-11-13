package io.dymatics.cogny.staff.service;

import io.dymatics.cogny.domain.model.Page;
import io.dymatics.cogny.domain.model.Repair;

public interface RepairService {
    Object populateRepairComponentList(Page page);

    Object getRepairCategoryEnums();

    Repair getRepair(Long repairNo);

    Object populateRepairList(Page page);

    Object saveRepair(Repair.Form form);

    Repair deleteRepair(Long repairNo);
}
