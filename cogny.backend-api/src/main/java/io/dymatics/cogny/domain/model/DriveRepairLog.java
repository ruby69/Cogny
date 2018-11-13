package io.dymatics.cogny.domain.model;

import java.io.Serializable;
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
public class DriveRepairLog implements Serializable {
    private static final long serialVersionUID = -5404265745555706987L;

    public enum Ref {
        DRIVE, REP, REPM;

        public boolean isRepair() {
            return this == REP;
        }

        public boolean isRepairMessage() {
            return this == REPM;
        }
    }

    @JsonIgnore private Long driveRepairLogNo;
    @JsonIgnore private Long vehicleNo;
    private int driveTime;
    private int driveMileage;
    private int totalMileage;
    private Ref ref;
    @JsonIgnore private Long refNo;
    @JsonIgnore private int dateIdx;
    private Date regDate;

    private static DriveRepairLog instance(Long vehicleNo, int driveTime, int driveMileage, int totalMileage, Ref ref, Long refNo, int dateIdx) {
        DriveRepairLog instance = new DriveRepairLog();
        instance.setVehicleNo(vehicleNo);
        instance.setDriveTime(driveTime);
        instance.setDriveMileage(driveMileage);
        instance.setTotalMileage(totalMileage);
        instance.setRef(ref);
        instance.setRefNo(refNo);
        instance.setDateIdx(dateIdx);
        return instance;
    }

    public static DriveRepairLog drive(Long vehicleNo, Long refNo, String dateIdx) {
        return instance(vehicleNo, 0, 0, 0, Ref.DRIVE, refNo, Integer.parseInt(dateIdx));
    }

    public static DriveRepairLog repair(Long vehicleNo, Long refNo) {
        return instance(vehicleNo, 0, 0, 0, Ref.REP, refNo, 0);
    }

    public static DriveRepairLog repairMsg(Long vehicleNo, Long refNo) {
        return instance(vehicleNo, 0, 0, 0, Ref.REPM, refNo, 0);
    }

    @Data
    @JsonInclude(Include.NON_NULL)
    public static class Summary implements Serializable {
        private static final long serialVersionUID = -5183595769587633219L;
        @JsonIgnore private int dateIdx;

        private int driveTime;
        private int driveMileage;
        private int totalMileage;
        private boolean hasRepair;
        private boolean hasRepairMsg;

        public Summary(Entry<Integer, List<DriveRepairLog>> entry) {
            dateIdx = entry.getKey();
            List<DriveRepairLog> list = entry.getValue();
            driveTime = list.stream().mapToInt(DriveRepairLog::getDriveTime).sum();
            driveMileage = list.stream().mapToInt(DriveRepairLog::getDriveMileage).sum();
            totalMileage = list.stream().mapToInt(DriveRepairLog::getTotalMileage).max().getAsInt();

            DriveRepairLog repair = list.stream().filter(e -> e.getRef().isRepair()).reduce((a, b) -> b).orElse(null);
            DriveRepairLog repairMsg = list.stream().filter(e -> e.getRef().isRepairMessage()).reduce((a, b) -> b).orElse(null);
            if (repair != null && repairMsg != null) {
                hasRepair = repair.getDriveRepairLogNo().longValue() > repairMsg.getDriveRepairLogNo().longValue();
                hasRepairMsg = repair.getDriveRepairLogNo().longValue() < repairMsg.getDriveRepairLogNo().longValue();
            } else {
                hasRepair = repair != null;
                hasRepairMsg = repairMsg != null;
            }
        }

        public String getIssuedDate() {
            String temp = String.valueOf(dateIdx);
            return temp.substring(4, 6) + "/" + temp.substring(6);
        }
    }

    @Data
    @JsonInclude(Include.NON_NULL)
    public static class Page implements Serializable {
        private static final long serialVersionUID = -217616745398265380L;

        private int page = 0;
        private int scale = 90;
        private List<Summary> contents;
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
        public Summary getLast() {
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
