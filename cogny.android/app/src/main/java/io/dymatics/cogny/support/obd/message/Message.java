package io.dymatics.cogny.support.obd.message;

import org.apache.commons.lang3.StringUtils;

import java.util.Iterator;
import java.util.List;

import io.dymatics.cogny.support.obd.Protocol;
import lombok.Getter;

public abstract class Message {
    @Getter private Protocol.Cmd.Read cmd;
    @Getter protected List<Byte> bytes;

    Integer convertSigned(byte signal, byte... values) {
        return isNull(values) ? null : toUnsignedInt(values) * (signal == 0 ? 1 : -1);
    }

    Integer convert(byte... values) {
        return isNull(values) ? null : toUnsignedInt(values);
    }

    private boolean isNull(byte... bytes) {
        for (byte b : bytes) {
            if (b != (byte)0xFF) {
                return false;
            }
        }
        return true;
    }

    int toUnsignedInt(byte... bytes) {
        return (int) toUnsignedLong(bytes);
    }

    int toSignedInt(byte... bytes) {
        return (int) toSignedLong(bytes);
    }

    long toUnsignedLong(byte... bytes) {
        long result = 0L;
        int factor = bytes.length;
        for(byte b : bytes) {
            result |= (b & 0xFFL) << (8 * (--factor));
        }

        return result;
    }

    long toSignedLong(byte... bytes) {
        long result = 0L;
        int i = bytes.length;
        for(byte b : bytes) {
            result |= b << (8 * (--i));
        }
        return result;
    }

    protected String toString(byte... bytes) {
        return new String(bytes);
    }

    public String toHexString() {
        StringBuilder sb = new StringBuilder();
        for(Iterator<Byte> it = bytes.iterator(); it.hasNext();) {
            sb.append(String.format("%02X", it.next()));
            if(it.hasNext()) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    public void populate(Protocol.Cmd.Read cmd, List<Byte> bytes) {
        this.cmd = cmd;
        this.bytes = bytes;
        doPopulate();
    }

    protected abstract void doPopulate();

    public abstract String[] toStringArray();

    public String getValueString() {
        return StringUtils.join(toStringArray(), ",");
    }
}
