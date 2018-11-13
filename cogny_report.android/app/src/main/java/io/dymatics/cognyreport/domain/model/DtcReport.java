package io.dymatics.cognyreport.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DtcReport implements Serializable {
    private static final long serialVersionUID = 6517880215970693656L;

    private String driveDate;
    private String licenseNo;
    private int dtcCount;
    private String model;
    private int odometer;
    private boolean driving;
    private Long vehicleNo;
    private Long obdDeviceNo;

    private ArrayList<DtcInfo> dtcs;
    private UserMobileDevice userMobileDevice;
    private RepairMsg repairMsg;
    private Map<Long, List<Diagnosis.Summary>> diagnosis;

    private boolean opened;

    public int getDiagnosisAndDtcCount() {
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

    public boolean hasFatal() {
        if (diagnosis != null && !diagnosis.isEmpty()) {
            Collection<List<Diagnosis.Summary>> values = diagnosis.values();
            for (Iterator<List<Diagnosis.Summary>> it = values.iterator(); it.hasNext();){
                List<Diagnosis.Summary> summaries = it.next();
                if (summaries != null && !summaries.isEmpty()) {
                    for (Diagnosis.Summary summary : summaries) {
                        if (summary.getDiagnosisResult().isFatal()) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DtcInfo implements Serializable {
        private static final long serialVersionUID = -6243841949944324007L;

        private String dtcCode;
        private String desc;
        private Date issuedTime;
    }

    @Data
    public static class Group implements Serializable {
        private static final long serialVersionUID = -8774221182186162788L;

        private String driveDate;
        private List<DtcReport> reports;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Groups extends ArrayList<Group> {
        private static final long serialVersionUID = -465259253434282709L;
    }
}
