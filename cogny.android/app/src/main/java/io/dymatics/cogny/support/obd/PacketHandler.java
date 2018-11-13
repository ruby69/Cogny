package io.dymatics.cogny.support.obd;

import java.util.ArrayList;
import java.util.List;

import io.dymatics.cogny.BuildConfig;
import io.dymatics.cogny.Constants;

public abstract class PacketHandler {
    protected final List<Byte> buffer = new ArrayList<>();
    protected int tryCount;

    protected abstract void doInitialize();
    protected abstract void doHandle(Packet packet);

    public void initialize() {
        buffer.clear();
        tryCount = 0;
        doInitialize();
    }

    public void handle(byte[] bytes) {
        for (byte b : bytes) {
            buffer.add(b);
        }
//         printBuffer();

        Packet packet = findPacket();
        if (packet != null) {
            Constants.log("RX <= " + packet.toHexString());
            doHandle(packet);
        }
    }

    private Packet findPacket() {
        int from = findStartPatternIndex(0);
        if (from > -1) {
            int to = from + 4;
            if (!Protocol.Cmd.isNoData(buffer.get(from + 1))) {
                to += computeLength(buffer.get(from + 2), buffer.get(from + 3));
            }

            if (to <= buffer.size()) {
                List<Byte> subList = new ArrayList<>(buffer.subList(from, to));
                Packet packet = new Packet(subList);
                if(packet.isValidate()) {
                    removeBuffer(to);
                    tryCount = 0;
                    return packet;
                }
            }
        }
        tryCount++;
        if (tryCount > 20) {
            List<Byte> loss = removeBuffer(findStartPatternIndex(from + 1));
            log(loss, "invalid RX <= ");
            tryCount = 0;
        }
        return null;
    }

    private int findStartPatternIndex(int from) {
        for(int i = from; i<buffer.size() - 1; i++) { // stx_cmd_lsb(len)_msb(len)
            if (Protocol.isStx(buffer.get(i)) && Protocol.Cmd.isReadable(buffer.get(i + 1))) {
                return i;
            }
        }
        return -1;
    }

    private int computeLength(byte... bytes) {
        int result = 0;
        int factor = bytes.length;
        for(byte b : bytes) {
            result |= (b & 0xFF) << (8 * (--factor));
        }

        return result;
    }

    private List<Byte> removeBuffer(int to) {
        List<Byte> bytes = new ArrayList<>(to);
        for(int i = 0; i<to; i++) {
            bytes.add(buffer.remove(0));
        }
        return bytes;
    }


    //////////////////////////////////////////////////////////////////////////////////////////////// debug

    protected void log(List<Byte> bytes, String prefix) {
        if (!BuildConfig.FLAVOR.equals("forRelease")) {
            StringBuilder sb = new StringBuilder(prefix);
            for (byte b : bytes) {
                sb.append(String.format("%02X ", b));
            }
            Constants.log(sb.toString());
        }
    }

//    private void printBuffer() {
//        StringBuilder sb = new StringBuilder();
//        for (byte b : buffer) {
//            sb.append(String.format("%02X ", b));
//        }
//        Log.e("seco_obd_tester", "BUFFER] " + sb.toString());
//    }
}
