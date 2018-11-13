package io.dymatics.cogny.event.obd;

import lombok.Getter;

public class ObdConnected {
    @Getter private String name;
    @Getter private String address;

    public ObdConnected(String name, String address) {
        this.name = name;
        this.address = address;
    }
}
