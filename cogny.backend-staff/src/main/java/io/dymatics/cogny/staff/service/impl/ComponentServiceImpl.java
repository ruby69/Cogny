package io.dymatics.cogny.staff.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.dymatics.cogny.domain.model.Component.ComponentForm;
import io.dymatics.cogny.domain.persist.ComponentRepository;
import io.dymatics.cogny.staff.service.ComponentService;

@Service
@Transactional(readOnly = true)
public class ComponentServiceImpl implements ComponentService {
    @Autowired private ComponentRepository componentRepository;
    
    @Override
    public Object getComponentCate1() {
        return componentRepository.findAllComponentCate1();
    }
    
    @Override
    public Object getComponentCate2ListbyCate1No(Long componentCate1No) {
        return componentRepository.findComponentCate2ListByCate1No(componentCate1No);
    }
    
    @Override
    public Object getComponentListByCate(ComponentForm componentForm) {
        return componentRepository.findComponentCateListByCate(componentForm);
    }

}
