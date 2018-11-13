package io.dymatics.cogny.support.obd;

import io.dymatics.cogny.support.obd.message.DtcMessage;
import io.dymatics.cogny.support.obd.message.Message;
import io.dymatics.cogny.support.obd.message.ObdInfoMessage;
import io.dymatics.cogny.support.obd.message.SensorMessage;
import lombok.Getter;

public interface Protocol {

    static boolean isStx(byte value) {
        return value == Header.STX.value;
    }

    static boolean isExt(byte value) {
        return value == Tail.EXT.value;
    }



    enum Header {
        STX((byte) 0xFA);

        private byte value;

        Header(byte value) {
            this.value = value;
        }
    }

    enum Tail {
        EXT((byte) 0xFB);

        private byte value;

        Tail(byte value) {
            this.value = value;
        }
    }

    interface Cmd {
        static boolean isReadable(byte value) {
            return (value == Read.LIVE.value || value == Read.UNTRANS.value || value == Read.DTC.value || value == Read.OBD.value || value == Read.ACK.value || value == Read.NACK.value || value == Read.DISCONNECT.value);
        }

        static boolean isNoData(byte value) {
            return (value == Read.ACK.value || value == Read.NACK.value || value == Read.DISCONNECT.value);
        }

        static boolean isSequential(byte value) {
            return (value == Read.LIVE.value || value == Read.UNTRANS.value || value == Read.DTC.value);
        }

        enum Read {
            LIVE((byte) 0x11, 112, SensorMessage.class),
            UNTRANS((byte) 0x14, 112, SensorMessage.class),
            DTC((byte) 0x16, 192, DtcMessage.class),
            OBD((byte) 0x19, 24, ObdInfoMessage.class),
            ACK((byte) 0x1A),
            NACK((byte) 0x1E),
            DISCONNECT((byte) 0x1F);

            private byte value;
            @Getter private int messageLength;
            @Getter private Class<? extends Message> clazz;

            Read(byte value) {
                this.value = value;
            }

            Read(byte value, int messageLength, Class<? extends Message> clazz) {
                this.value = value;
                this.messageLength = messageLength;
                this.clazz = clazz;
            }

            Read(byte value, Class<? extends Message> clazz) {
                this.value = value;
                this.messageLength = 0;
                this.clazz = clazz;
            }

            public boolean isObdInfo() {
                return this == OBD;
            }

            public boolean isNoData() {
                return this == ACK || this == NACK || this == DISCONNECT;
            }

            public boolean isAck() {
                return this == ACK;
            }

            public boolean isDisconnect() {
                return this == DISCONNECT;
            }

            public boolean isUntrans() {
                return this == UNTRANS;
            }

            public static Read valueOf(byte value) {
                if (value == LIVE.value) {
                    return LIVE;
                } else if (value == UNTRANS.value) {
                    return UNTRANS;
                } else if (value == DTC.value) {
                    return DTC;
                } else if (value == OBD.value) {
                    return OBD;
                } else if (value == ACK.value) {
                    return ACK;
                } else if (value == NACK.value) {
                    return NACK;
                } else if (value == DISCONNECT.value) {
                    return DISCONNECT;
                }

                throw new IllegalArgumentException();
            }
        }

        enum Write {
            DTC((byte) 0x49),
            REQ((byte) 0x50),
            RTC((byte) 0x51),
            ERASE_UNTRANS((byte) 0x52),
            FLASH_ERASE((byte) 0x53),
            NEW_FW_FOTA((byte) 0x54),
            CON_FW_FOTA((byte) 0x55),
            NEW_TABLE_FOTA((byte) 0x56),
            FW_ROLLBACK((byte) 0x57),
            REBOOT((byte) 0x58),
            ACK((byte) 0x5A),
            NACK((byte) 0x5E),
            DISCONNECT((byte) 0x5F);

            @Getter private byte value;

            Write(byte value) {
                this.value = value;
            }

            public boolean isDisconnect() {
                return this == DISCONNECT;
            }

            public boolean isAck() {
                return this == ACK;
            }

            public static byte[] getBytes(Protocol.Cmd.Write cmd) {
                return new byte[]{Header.STX.value, cmd.value, (byte) (Header.STX.value ^ cmd.value), Tail.EXT.value};
            }

            public static byte[] getBytes(Protocol.Cmd.Write cmd, byte[] payload) {
                return getBytes(cmd, payload, 1, 1);
            }

            public static byte[] getBytes(Protocol.Cmd.Write cmd, byte[] payload, int cCount, int tCount) {
                int allLength = payload.length + 8;
                byte[] lengthBytes = toBytes(payload.length + 4);

                byte[] bytes = new byte[allLength];
                bytes[0] = Protocol.Header.STX.value;
                bytes[1] = cmd.getValue();
                bytes[2] = lengthBytes[0];
                bytes[3] = lengthBytes[1];
                bytes[4] = (byte) cCount;
                bytes[5] = (byte) tCount;

                for (int i = 0; i<payload.length; i++) {
                    bytes[i+6] = payload[i];
                }
                bytes[allLength-2] = checksum(bytes);
                bytes[allLength-1] = Protocol.Tail.EXT.value;

                return bytes;
            }

            private static byte checksum(byte[] bytes) {
                byte checksum = 0x00;
                for(int i = 0; i<bytes.length - 2; i++) {
                    checksum ^= bytes[i];
                }
                return checksum;
            }

            private static byte[] toBytes(int value) {
                return new byte[] {(byte)(value >> 8), (byte)(value)};
            }
        }
    }
}
