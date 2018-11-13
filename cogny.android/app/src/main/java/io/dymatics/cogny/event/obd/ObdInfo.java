package io.dymatics.cogny.event.obd;

import io.dymatics.cogny.support.obd.message.ObdInfoMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class ObdInfo {
    @Getter private ObdInfoMessage obdInfoMessage;
}
