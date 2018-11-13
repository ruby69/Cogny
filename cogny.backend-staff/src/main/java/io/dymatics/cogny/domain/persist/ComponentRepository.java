package io.dymatics.cogny.domain.persist;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import io.dymatics.cogny.domain.model.Component;
import io.dymatics.cogny.domain.model.Component.ComponentCate1;
import io.dymatics.cogny.domain.model.Component.ComponentCate2;
import io.dymatics.cogny.domain.model.Component.ComponentForm;

@Mapper
public interface ComponentRepository {

    List<ComponentCate1> findAllComponentCate1();
    
    List<ComponentCate2> findComponentCate2ListByCate1No(Long componentCate1No);
    
    List<Component> findComponentCateListByCate(ComponentForm componentForm);

}
