package io.dymatics.cogny.event.obd;

import android.bluetooth.BluetoothDevice;

import lombok.Getter;

public class ObdDetected {
    @Getter private BluetoothDevice device;

    public ObdDetected(BluetoothDevice device) {
        this.device = device;
    }
}
