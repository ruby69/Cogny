package io.dymatics.cogny.support.obd;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.dymatics.cogny.support.obd.message.Message;
import lombok.Getter;

public class Packet {
    private List<Byte> bytes;
    @Getter private List<Message> messages;
    @Getter private boolean validate;
    @Getter private Protocol.Cmd.Read cmd;

    public Packet(List<Byte> bytes) {
        this.bytes = bytes;
        cmd = Protocol.Cmd.Read.valueOf(bytes.get(1));
        if (validate()) {
            parse();
        }
    }

    private boolean validate() {
        if (cmd.isNoData()) {
            if (bytes.size() < 4) {
                return validate = false;
            }

            byte stx = bytes.get(0);
            byte computedChecksum = checksum();
            byte compareChecksum = bytes.get(2);
            byte ext = bytes.get(3);
            return validate = Protocol.isStx(stx) && Protocol.isExt(ext) && computedChecksum == compareChecksum;
        } else{
            if (bytes.size() < 9) {
                return validate = false;
            }

            byte stx = bytes.get(0);
            byte cmd = bytes.get(1);
            byte ext = bytes.get(bytes.size() - 1);
            byte computedChecksum = checksum();
            byte compareChecksum = bytes.get(bytes.size() - 2);
            return validate = Protocol.isStx(stx) && Protocol.Cmd.isReadable(cmd) && Protocol.isExt(ext) && computedChecksum == compareChecksum;
        }
    }

    private void parse() {
        if (!cmd.isNoData()) {
            List<Byte> messageBytes = bytes.subList(6, bytes.size() - 2);
            if (!cmd.isUntrans()) {
                messages = new ArrayList<>(1);
                Message message = newMessage(messageBytes);
                if (message != null) {
                    messages.add(message);
                }
            } else {
                int messageLength = messageBytes.size();
                if (messageLength < 113) { // case of LIVE or Single UNTRANS
                    messages = new ArrayList<>(1);
                    Message message = newMessage(messageBytes);
                    if (message != null) {
                        messages.add(message);
                    }

                } else { // case of Multiple UNTRANS
                    messages = new ArrayList<>(2);
                    int len = messageLength / 2;
                    for (int i = 0; i<2; i++) {
                        int from = len * i;
                        int to = from + len;
                        Message message = newMessage(messageBytes.subList(from, to));
                        if (message != null) {
                            messages.add(message);
                        }
                    }
                }
            }
        }
    }

    private Message newMessage(List<Byte> messageBytes) {
        try {
            Message message = cmd.getClazz().newInstance();
            message.populate(cmd, messageBytes);
            return message;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private byte checksum() {
        byte checksum = 0x00;
        for(int i = 0; i<bytes.size() - 2; i++) {
            checksum ^= bytes.get(i);
        }
        return checksum;
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
}
