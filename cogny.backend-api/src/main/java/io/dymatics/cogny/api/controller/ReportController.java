package io.dymatics.cogny.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.dymatics.cogny.api.service.ReportService;
import io.dymatics.cogny.domain.model.PerformHistory;
import io.dymatics.cogny.domain.model.Repair;
import io.dymatics.cogny.domain.model.RepairMsg;
import io.dymatics.cogny.domain.model.User;

@RestController
public class ReportController {
    @Autowired private ReportService dtcReportService;
    @Value("#{taskExecutor}") private ThreadPoolTaskExecutor taskExecutor;

    @RequestMapping(value = "report/list", method = RequestMethod.GET)
    public Object list(User user) {
        return dtcReportService.dtcReportsByPartner(user.getPartnerNo());
    }

    @RequestMapping(value = "report/diagcate", method = RequestMethod.GET)
    public Object diagnosisCategories() {
        return dtcReportService.diagnosisCategories();
    }

    @RequestMapping(value = "report/performs/{vehicleNo}", method = RequestMethod.GET)
    public Object performs(@PathVariable("vehicleNo") Long vehicleNo, PerformHistory.Page page) {
        page.param("vehicleNo", vehicleNo);
        return dtcReportService.populatePerforms(page).clear();
    }

    @RequestMapping(value = "report/repair/noti", method = RequestMethod.POST)
    public Object repairNoti(User user, @RequestBody RepairMsg.Form form) {
        form.setSendUserNo(user.getUserNo());
        return dtcReportService.repairNoti(form);
    }

    @RequestMapping(value = "report/repairs", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void repairs(User user, @RequestBody Repair.Form form) {
        form.setRepairUserNo(user.getUserNo());
        dtcReportService.repairs(form);
    }
}
