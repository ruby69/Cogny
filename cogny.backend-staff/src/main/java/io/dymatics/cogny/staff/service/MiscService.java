package io.dymatics.cogny.staff.service;

public interface MiscService {

    Object getFuels();

    Object getManufacturers();

    Object getModelGouprs(Long manufacturerNo);

    Object getModels(Long modelGroupNo);

    Object getModelSalesYear(Long modelNo);
}
