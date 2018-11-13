package io.dymatics.cogny.domain.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class PerformHistory implements Serializable {
    private static final long serialVersionUID = 2556666517031479324L;

    public enum Ref {
        DTC, REP, REPM, DIAG
    }

    @JsonIgnore private Long performHistoryNo;
    private String title;
    private String body;
    @JsonIgnore private Long vehicleNo;
    private Ref ref;
    @JsonIgnore private Long refNo;
    @JsonIgnore private int dateIdx;
    private Date regDate;
    private Date issuedTime;

    private static PerformHistory instance(Long vehicleNo, String title, String body, Ref ref, Long refNo, int dateIdx) {
        PerformHistory instance = new PerformHistory();
        instance.setTitle(title);
        instance.setBody(body);
        instance.setVehicleNo(vehicleNo);
        instance.setRef(ref);
        instance.setRefNo(refNo);
        instance.setDateIdx(dateIdx);
        return instance;
    }

    public static PerformHistory diagnosis(Long vehicleNo, String title, String body, Long refNo) {
        return instance(vehicleNo, title, body, Ref.DIAG, refNo, 0);
    }

    public static PerformHistory repair(Long vehicleNo, String title, Long refNo) {
        return instance(vehicleNo, title, null, Ref.REP, refNo, 0);
    }

    public static PerformHistory repairMsg(Long vehicleNo, String title, Long refNo) {
        return instance(vehicleNo, title, null, Ref.REPM, refNo, 0);
    }

    @Data
    @JsonInclude(Include.NON_NULL)
    public static class Group implements Serializable {
        private static final long serialVersionUID = 6326515573585041149L;
        @JsonIgnore private int dateIdx;
        private List<PerformHistory> list;
        private static final Comparator<PerformHistory> COMPARATOR = new Comparator<PerformHistory>(){
            @Override
            public int compare(PerformHistory o1, PerformHistory o2) {
                Date issuedTime1 = o1.getIssuedTime();
                Date date1 = issuedTime1 != null ? issuedTime1 : o1.getRegDate();
                Date issuedTime2 = o2.getIssuedTime();
                Date date2 = issuedTime2 != null ? issuedTime2 : o2.getRegDate();

                if (date1 != null && date2 != null) {
                    long time1 = date1.getTime();
                    long time2 = date2.getTime();
                    return time1 > time2 ? -1 : time1 < time2 ? 1 : 0;
                } else {
                    return 0;
                }
            }
        };

        public Group(Entry<Integer, List<PerformHistory>> entry) {
            dateIdx = entry.getKey();
            list = entry.getValue();
            if (list != null && list.size() > 1) {
                Collections.sort(list, COMPARATOR);
            }
        }

        public String getIssuedDate() {
            String temp = String.valueOf(dateIdx);
            return temp.substring(0, 4) + "-" + temp.substring(4, 6) + "-" + temp.substring(6);
        }
    }

    @Data
    @JsonInclude(Include.NON_NULL)
    public static class Page implements Serializable {
        private static final long serialVersionUID = -217616745398265380L;

        private int page = 0;
        private int scale = 90;
        private List<Group> contents;
        private boolean hasMore;
        private Map<String, Object> p = new HashMap<String, Object>();

        public void setPage(Integer page) {
            this.page = page == null || page.intValue() < 0 ? 0 : page.intValue();
        }

        public Page clear() {
            p.clear();
            return this;
        }

        public Page param(String key, Object value) {
            p.put(key, value);
            return this;
        }

        @JsonIgnore
        public Group getLast() {
            if(hasContents()) {
                return contents.get(contents.size() - 1);
            }
            return null;
        }

        private boolean hasContents() {
            return contents != null && !contents.isEmpty();
        }
    }
}
