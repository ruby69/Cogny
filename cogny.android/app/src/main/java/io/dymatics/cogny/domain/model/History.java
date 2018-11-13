package io.dymatics.cogny.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.apache.commons.lang3.time.FastDateFormat;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class History implements Serializable {
    private static final long serialVersionUID = -5183595769587633219L;

    private String issuedDate;
    private int driveTime;
    private int driveMileage;
    private int totalMileage;
    private boolean hasRepair;
    private boolean hasRepairMsg;

    private static final FastDateFormat DF = FastDateFormat.getInstance("MM/dd", TimeZone.getDefault(), Locale.getDefault());

    public String getDisplayIssuedDate() {
        if (DF.format(Calendar.getInstance()).equals(issuedDate)) {
            return "오늘";
        }
        return issuedDate;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Page implements Serializable {
        private static final long serialVersionUID = -217616745398265380L;

        private int page = 0;
        private int scale = 90;
        private List<History> contents;
        private boolean hasMore;
    }
}
