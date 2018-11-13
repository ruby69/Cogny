package io.dymatics.cogny.jobs.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import io.dymatics.cogny.jobs.service.DiagnosisExecuteService;
import io.dymatics.cogny.jobs.service.ScheduleService;
import lombok.extern.slf4j.Slf4j;

@Service
@Profile(value = { "product", "test" })
@Slf4j
public class ScheduleServiceImpl implements ScheduleService {
    @Autowired private DiagnosisExecuteService diagnosisExecuteService;

    @Override
    // @Scheduled(cron="0 0 0 * * ?") // 0초, 0분, 0시, 매일, 매달, 아무요일
    @Scheduled(fixedDelay = 10000L, initialDelay = 10000L) // 10초간격, 최초 10초후 실행.
    public void scheduledJob() {
        try {
//            log.info("do diagnosisBatch");
             diagnosisExecuteService.diagnosisBatch();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
