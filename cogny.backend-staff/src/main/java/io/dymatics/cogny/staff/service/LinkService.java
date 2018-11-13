package io.dymatics.cogny.staff.service;

import io.dymatics.cogny.domain.model.LinkForm;
import io.dymatics.cogny.domain.model.User;

public interface LinkService {

    Object linkVehicleObd(User currentUser, LinkForm form);

    Object linkObdVehicle(User currentUser, LinkForm form);

}
