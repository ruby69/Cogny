package io.dymatics.cogny.api.service;

import io.dymatics.cogny.domain.model.PerformHistory;
import io.dymatics.cogny.domain.model.Repair;
import io.dymatics.cogny.domain.model.RepairMsg;

public interface ReportService {

    Object dtcReportsByPartner(Long partnerNo);

    PerformHistory.Page populatePerforms(PerformHistory.Page page);

    Object repairNoti(RepairMsg.Form form);

    void repairs(Repair.Form form);

    Object diagnosisCategories();

}
