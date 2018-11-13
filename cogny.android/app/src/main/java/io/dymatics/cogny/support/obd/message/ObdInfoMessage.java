package io.dymatics.cogny.support.obd.message;

import java.util.Locale;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Data
@EqualsAndHashCode(callSuper = false)
public class ObdInfoMessage extends Message{
    @Getter protected String fwVersion;
    @Getter protected String tableVersion;
    @Getter protected String rtcTime;
    @Getter protected int untransCount;
    @Getter protected boolean existCanBusError;
    @Getter protected boolean existFwFota;
    @Getter protected boolean canFwRollback;
    @Getter protected String serial;

    @Override
    protected void doPopulate() {
        fwVersion = String.format(Locale.getDefault(), "%d%d%d", bytes.get(0), bytes.get(1), bytes.get(2));
        tableVersion = String.format(Locale.getDefault(), "%d%d", bytes.get(3), bytes.get(4));
        rtcTime = String.format(Locale.getDefault(), "%02d-%02d-%02d %02d:%02d:%02d", bytes.get(5), bytes.get(6), bytes.get(7), bytes.get(8), bytes.get(9), bytes.get(10));
        untransCount = toUnsignedInt(bytes.get(11), bytes.get(12));
        existCanBusError = bytes.get(13) == 0;
        existFwFota = bytes.get(14) == 1;
        canFwRollback = bytes.get(15) == 1;
        serial = new String(new byte[]{bytes.get(16), bytes.get(17), bytes.get(18), bytes.get(19), bytes.get(20), bytes.get(21), bytes.get(22), bytes.get(23)});
    }

    @Override
    public String[] toStringArray() {
        return new String[] {
                serial,
                fwVersion,
                tableVersion,
                rtcTime,
                String.valueOf(untransCount),
                existCanBusError ? "0" : "1",
                existFwFota ? "1" : "0",
                canFwRollback ? "1" : "0"
        };
    }
}
