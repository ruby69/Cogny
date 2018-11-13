package io.dymatics.cogny.staff.service;

import io.dymatics.cogny.domain.model.Page;
import io.dymatics.cogny.domain.model.User;
import io.dymatics.cogny.domain.model.Vehicle;

public interface VehicleService {

    Object populateVehicles(Page page);

    Vehicle getVehicle(Long vehicleNo);

    Object saveVehicle(Vehicle vehicle);

    Vehicle deleteVehicle(User currentUser, Long vehicleNo);

    Object getFreeVehicles();
    
    boolean checkAuthority(User currentUser, Long vehicleNo);

}
