package io.dymatics.cogny.staff.service;

import io.dymatics.cogny.domain.model.Component.ComponentForm;

public interface ComponentService {
    Object getComponentCate1();

    Object getComponentCate2ListbyCate1No(Long componentCate1No);

    Object getComponentListByCate(ComponentForm componentForm);
}
