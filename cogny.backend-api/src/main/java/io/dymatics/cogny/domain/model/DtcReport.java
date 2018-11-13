package io.dymatics.cogny.domain.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.dymatics.cogny.domain.model.MobileDevice.UserMobileDevice;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(of = { "driveDate", "licenseNo", "dtcCount"})
@JsonInclude(Include.NON_NULL)
public class DtcReport implements Serializable {
    private static final long serialVersionUID = 6517880215970693656L;

    private String driveDate;
    private Date endTime;
    private String licenseNo;
    private int dtcCount;
    private String model;
    private int odometer;
    private boolean driving;
    private Long vehicleNo;
    private Long obdDeviceNo;
    private Long driveHistoryNo;

    private UserMobileDevice userMobileDevice;
    private List<DtcInfo> dtcs;
    private RepairMsg repairMsg;
    private Map<Long, List<Diagnosis.Summary>> diagnosis;

    @JsonIgnore
    public int countOf() {
        int diagnosisCount = 0;
        if (diagnosis == null || diagnosis.isEmpty()) {
            diagnosisCount = 0;
        } else {
            for (List<Diagnosis.Summary> list : diagnosis.values()) {
                for (Diagnosis.Summary s : list) {
                    if (s.isShowDescription() && !s.isTireNormal()) {
                        diagnosisCount++;
                    }
                }
            }
        }

        int dtcCount = dtcs == null || dtcs.isEmpty() ? 0 : dtcs.size();
        return diagnosisCount + dtcCount;
    }

    @Data
    @ToString(of = { "dtcCode"})
    @JsonInclude(Include.NON_NULL)
    public static class DtcInfo implements Serializable {
        private static final long serialVersionUID = -6243841949944324007L;

        private String dtcCode;
        private String desc;
        private Date issuedTime;
    }

    @Data
    @JsonInclude(Include.NON_NULL)
    public static class GroupDate implements Serializable {
        private static final long serialVersionUID = -8774221182186162788L;

        private static final Comparator<DtcReport> comparator = new Comparator<DtcReport> () {
            @Override
            public int compare(DtcReport o1, DtcReport o2) {
                if (o1.countOf() < o2.countOf()) {
                    return 1;
                } else if (o1.countOf() > o2.countOf()) {
                    return -1;
                }
                return 0;
            }
        };

        private String driveDate;
        private List<DtcReport> reports;

        public GroupDate(String driveDate, List<DtcReport> reports) {
            this.driveDate = driveDate;
            this.reports = reports;
            if (reports != null && reports.size() > 1) {
                Collections.sort(reports, comparator);
            }
        }
    }
}
