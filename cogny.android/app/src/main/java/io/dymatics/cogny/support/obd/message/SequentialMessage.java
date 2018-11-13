package io.dymatics.cogny.support.obd.message;

import org.apache.commons.lang3.ArrayUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.dymatics.cogny.Constants;
import lombok.Getter;

public abstract class SequentialMessage extends Message {
    @Getter protected long seq;
    @Getter protected String time;

    protected static final int OFFSET = 9;

    protected void parseSequence() {
        seq = toUnsignedLong(bytes.get(0), bytes.get(1), bytes.get(2), bytes.get(3));
        time = String.format(Locale.getDefault(), "%02d-%02d-%02d %02d:%02d:%02d", bytes.get(4), bytes.get(5), bytes.get(6), bytes.get(7), bytes.get(8), bytes.get(9));
    }

    protected abstract String[] getStringArray();

    @Override
    public String[] toStringArray() {
        return ArrayUtils.addAll(new String[]{String.valueOf(seq), time}, getStringArray());
    }

    public Date getDate() {
        try {
            return Constants.FORMAT_DATE_YMD_OBD.parse(time);
        } catch (ParseException e) {
            return new Date();
        }
    }
}
