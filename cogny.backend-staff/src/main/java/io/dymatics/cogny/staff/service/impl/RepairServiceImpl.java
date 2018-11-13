package io.dymatics.cogny.staff.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.dymatics.cogny.domain.model.EnumToMap;
import io.dymatics.cogny.domain.model.Page;
import io.dymatics.cogny.domain.model.Repair;
import io.dymatics.cogny.domain.model.Repair.RepairCategory;
import io.dymatics.cogny.domain.model.Repair.RepairComponent;
import io.dymatics.cogny.domain.persist.RepairRepository;
import io.dymatics.cogny.staff.service.RepairService;

@Service
@Transactional(readOnly = true)
public class RepairServiceImpl implements RepairService {
    @Autowired private RepairRepository repairRepository;
    
    @Override
    public Object populateRepairComponentList(Page page) {
        page.setTotal(repairRepository.countRepairComponentListByPage(page));
        page.setContents(repairRepository.findRepairComponentListByPage(page));
        return page;
    }
    
    @Override
    public Object getRepairCategoryEnums() {
        Map<String, Object> returnEnums = new HashMap<String, Object>();
        returnEnums.put("repairCategoryEnums", EnumToMap.getEnumList(RepairCategory.values()));
        return returnEnums;
    }
    
    @Override
    public Repair getRepair(Long repairNo) {
        return repairRepository.findByNo(repairNo);
    }
    
    @Override
    public Object populateRepairList(Page page) {
        page.setTotal(repairRepository.countRepairListByPage(page));
        page.setContents(repairRepository.findRepairListByPage(page));
        return page;
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public Object saveRepair(Repair.Form form) {
        if(form.getRepairNo() != null) {
            repairRepository.updateRepair(form);
            for(RepairComponent repairComponent : form.getRepairComponentList()) {
                if(repairComponent.getRepairComponentNo() == null) {
                    repairComponent.setRepairNo(form.getRepairNo());
                    repairRepository.insertRepairComponent(repairComponent);
                } else if (repairComponent.getEnabled() == null || repairComponent.getEnabled() == true) {
                    repairComponent.setRepairNo(form.getRepairNo());
                    repairRepository.updateRepairComponent(repairComponent);
                } else if (repairComponent.getEnabled() == false) {
                    repairRepository.deleteRepairComponent(repairComponent.getRepairComponentNo());
                } else {
                    try {
                        throw new Exception("fail to update repair: " + form.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    } 
                }
            }
        } else {
            repairRepository.insertRepair(form);
            repairRepository.insertRepairComponentList(form);
        }
        return getRepair(form.getRepairNo());
    }
    
    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public Repair deleteRepair(Long repairNo) {
        repairRepository.deleteRepairComponentByRepairNo(repairNo);
        repairRepository.deleteRepair(repairNo);
        return getRepair(repairNo);
    }
    
}
