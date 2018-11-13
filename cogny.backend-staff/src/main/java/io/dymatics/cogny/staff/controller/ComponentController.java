package io.dymatics.cogny.staff.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.dymatics.cogny.domain.model.Component.ComponentForm;
import io.dymatics.cogny.staff.service.ComponentService;

@RestController
public class ComponentController {
    @Autowired private ComponentService componentService;
    
    @RequestMapping(value = "components/cate1", method = RequestMethod.GET)
    public Object componentCate1() {
        return componentService.getComponentCate1();
    }
    
    @RequestMapping(value = "components/cate2", method = RequestMethod.GET)
    public Object componentCate2List(@RequestParam("componentCate1No") Long componentCate1No) {
        return componentService.getComponentCate2ListbyCate1No(componentCate1No);
    }
    
    @RequestMapping(value = "components", method = RequestMethod.GET)
    public Object componentList(ComponentForm componentForm) {
        return componentService.getComponentListByCate(componentForm);
    }
}