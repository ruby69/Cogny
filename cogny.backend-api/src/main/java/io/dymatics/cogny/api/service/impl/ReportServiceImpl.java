package io.dymatics.cogny.api.service.impl;

import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.api.core.ApiFuture;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;

import io.dymatics.cogny.api.service.ReportService;
import io.dymatics.cogny.domain.model.Diagnosis;
import io.dymatics.cogny.domain.model.DriveRepairLog;
import io.dymatics.cogny.domain.model.DtcReport;
import io.dymatics.cogny.domain.model.DtcReport.DtcInfo;
import io.dymatics.cogny.domain.model.MobileDevice.UserMobileDevice;
import io.dymatics.cogny.domain.model.PerformHistory;
import io.dymatics.cogny.domain.model.Repair;
import io.dymatics.cogny.domain.model.RepairMsg;
import io.dymatics.cogny.domain.model.RepairMsg.Msg;
import io.dymatics.cogny.domain.persist.DiagnosisRepository;
import io.dymatics.cogny.domain.persist.DriveRepairLogRepository;
import io.dymatics.cogny.domain.persist.DtcHistoryRepository;
import io.dymatics.cogny.domain.persist.DtcReportRepository;
import io.dymatics.cogny.domain.persist.PerformHistoryRepository;
import io.dymatics.cogny.domain.persist.RepairMsgRepository;
import io.dymatics.cogny.domain.persist.RepairRepository;
import io.dymatics.cogny.domain.persist.UserMobileDeviceRepository;

@Service
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {
    @Autowired private DtcReportRepository dtcReportRepository;
    @Autowired private DtcHistoryRepository dtcHistoryRepository;
    @Autowired private RepairMsgRepository repairMsgRepository;
    @Autowired private RepairRepository repairRepository;
    @Autowired private DiagnosisRepository diagnosisRepository;
    @Autowired private UserMobileDeviceRepository userMobileDeviceRepository;
    @Autowired private PerformHistoryRepository performHistoryRepository;
    @Autowired private DriveRepairLogRepository driveRepairLogRepository;

    private static final Comparator<Entry<String, List<DtcReport>>> COMPARATOR_REPORT = Comparator.comparing(Map.Entry::getKey);
    private static final Comparator<Entry<Integer, List<PerformHistory>>> COMPARATOR_PERFORM_HISTORY = Comparator.comparing(Map.Entry::getKey);

    @Override
    public Object dtcReportsByPartner(Long partnerNo) {
        List<DtcReport> dtcReports = dtcReportRepository.findByPartner(partnerNo);
        for (DtcReport dtcReport : dtcReports) {
            List<DtcInfo> dtcs = dtcReport.getDtcs();
            List<DtcInfo> distincted = dtcs.stream().collect(Collectors.toMap(DtcInfo::getDtcCode, p -> p, (p, q) -> p)).values().stream().sorted(Comparator.comparing(DtcInfo::getIssuedTime).reversed()).collect(Collectors.toList());
            dtcReport.setDtcs(distincted);
            dtcReport.setRepairMsg(repairMsgRepository.findLastRepairMsgByVehicleNo(dtcReport.getVehicleNo()));

            List<Diagnosis.Summary> list = diagnosisRepository.findLastSummariesByVehicleNo(dtcReport.getVehicleNo());
            dtcReport.setDiagnosis(list.stream().collect(Collectors.groupingBy(Diagnosis.Summary::getDiagnosisCateNo)));
        }

        Map<String, List<DtcReport>> groups = dtcReports.stream().collect(Collectors.groupingBy(DtcReport::getDriveDate));
        return groups.entrySet().stream()
                .sorted(COMPARATOR_REPORT.reversed())
                .map(it -> new DtcReport.GroupDate(it.getKey(), it.getValue()))
                .collect(Collectors.toList());
    }

    @Override
    public Object diagnosisCategories() {
        return diagnosisRepository.findDiagnosisCategory();
    }

    @Override
    public PerformHistory.Page populatePerforms(PerformHistory.Page page) {
        manipulatePageParams(page);

        List<PerformHistory> list = performHistoryRepository.findByPage(page);
        Map<Integer, List<PerformHistory>> groups = list.stream().collect(Collectors.groupingBy(PerformHistory::getDateIdx));
        page.setContents(groups.entrySet().stream()
                .sorted(COMPARATOR_PERFORM_HISTORY.reversed())
                .map(it -> new PerformHistory.Group(it))
                .collect(Collectors.toList()));

        PerformHistory.Group last = page.getLast();
        if (last != null) {
            Integer lastDateIdx = performHistoryRepository.findLastDateIdxByPage(page);
            if (lastDateIdx == null) {
                page.setHasMore(false);
            } else {
                page.setHasMore(last.getDateIdx() > lastDateIdx.intValue());
            }
        } else {
            page.setHasMore(false);
        }

        return page;
    }

    private FastDateFormat DF = FastDateFormat.getInstance("yyyyMMdd", TimeZone.getDefault(), Locale.getDefault());
    private PerformHistory.Page manipulatePageParams(PerformHistory.Page page) {
        int pageNo = page.getPage();
        int scale = page.getScale();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -(scale * pageNo));
        page.param("B", DF.format(calendar));

        calendar.add(Calendar.DAY_OF_MONTH, -(scale - 1));
        page.param("A", DF.format(calendar));

        return page;
    }


    @Override
    @Transactional(readOnly = false, noRollbackFor = Exception.class)
    public Object repairNoti(RepairMsg.Form form) {
        Long vehicleNo = form.getVehicleNo();
        repairMsgRepository.updateRepairMsgStatus(vehicleNo);

        RepairMsg repairMsg = form.getRepairMsg();
        repairMsgRepository.insert(repairMsg);

        Msg repairMsgType = repairMsg.getMsgType();
        Long repairMsgNo = repairMsg.getRepairMsgNo();
        if (!repairMsgType.isEmpty()) {
            performHistoryRepository.insert(PerformHistory.repairMsg(vehicleNo, repairMsgType.isEmergency() ? "긴급 점검 안내" : "일반 점검 안내", repairMsgNo));
            driveRepairLogRepository.insert(DriveRepairLog.repairMsg(vehicleNo, repairMsgNo));
        }

        dtcHistoryRepository.findRepairNoIsNull(vehicleNo).stream().forEach(dtcHistory -> repairMsgRepository.insertRepairMsgDtcHistory(repairMsgNo, dtcHistory.getDtcHistoryNo()));
        diagnosisRepository.findRepairNoIsNull(vehicleNo).stream().forEach(diagnosis -> repairMsgRepository.insertRepairMsgDiagnosis(repairMsgNo, diagnosis.getDiagnosisNo()));

        RepairMsg.Msg msgType = form.getMsgType();
        if (!msgType.isEmpty()) {
            noti(form.getRecvUserMobileDeviceNo(), msgType);
        }

        return repairMsgRepository.findLastRepairMsgByVehicleNo(vehicleNo);
    }

    private void noti(Long recvUserMobileDeviceNo, RepairMsg.Msg msg) {
        if (recvUserMobileDeviceNo != null) {
            UserMobileDevice userMobileDevice = userMobileDeviceRepository.findByNo(recvUserMobileDeviceNo);
            if (userMobileDevice != null) {
                AndroidNotification noti = AndroidNotification.builder().setSound("default").setTag(msg.name()).setBody(msg.getMessage()).build();
                AndroidConfig androidConfig = AndroidConfig.builder()
                        .setTtl(0L)
                        .setPriority(AndroidConfig.Priority.NORMAL)
                        .setNotification(noti)
                        .build();

                Message message = Message.builder().setToken(userMobileDevice.getFcmToken()).setAndroidConfig(androidConfig).build();
                ApiFuture<String> sendAsync = FirebaseMessaging.getInstance().sendAsync(message);
                try {
                    sendAsync.get();
                } catch (Exception e) {
                    e.printStackTrace();
                    // 전송실패
                }
            }
        }
    }

    @Override
    @Transactional(readOnly = false, noRollbackFor = Exception.class)
    public void repairs(Repair.Form form) {
        Repair repair = form.getRepair();
        repairRepository.insert(repair);

        Long vehicleNo = form.getVehicleNo();
        Long repairNo = repair.getRepairNo();

        dtcHistoryRepository.updateRepairNo(repairNo, vehicleNo);
        diagnosisRepository.updateRepairNo(repairNo, vehicleNo);
        repairMsgRepository.updateRepairNo(repairNo, vehicleNo);
        performHistoryRepository.insert(PerformHistory.repair(vehicleNo, "점검완료", repairNo));
        driveRepairLogRepository.insert(DriveRepairLog.repair(vehicleNo, repairNo));

        noti(form.getRecvUserMobileDeviceNo(), RepairMsg.Msg.COMPLETE);
    }

}
