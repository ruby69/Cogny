package io.dymatics.cogny.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.dymatics.cogny.api.service.LogService;
import io.dymatics.cogny.domain.model.ActivityLog;
import io.dymatics.cogny.domain.persist.ActivityLogRepository;

@Service
@Transactional(readOnly = true)
public class LogServiceImpl implements LogService {
    @Autowired private ActivityLogRepository activityLogRepository;

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void saveActivityLogs(List<String> logs) {
        List<ActivityLog> instnaces = ActivityLog.getInstnaces(logs);
        for (ActivityLog activityLog : instnaces) {
            if (!existActivityLog(activityLog)) {
                activityLogRepository.insert(activityLog);
            }
        }
    }

    private boolean existActivityLog(ActivityLog activityLog) {
        return activityLogRepository.countOfUserNoAndActivitySeq(activityLog) > 0L;
    }

}
