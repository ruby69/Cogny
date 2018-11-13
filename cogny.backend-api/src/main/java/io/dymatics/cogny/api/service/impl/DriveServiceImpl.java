package io.dymatics.cogny.api.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Entity.Builder;
import com.google.cloud.datastore.KeyFactory;
import com.google.common.collect.Lists;

import io.dymatics.cogny.api.service.DriveService;
import io.dymatics.cogny.domain.model.DriveHistory;
import io.dymatics.cogny.domain.model.DriveRepairLog;
import io.dymatics.cogny.domain.model.DtcRaw;
import io.dymatics.cogny.domain.model.SensingLog;
import io.dymatics.cogny.domain.model.SensingRaw;
import io.dymatics.cogny.domain.model.Sensings;
import io.dymatics.cogny.domain.persist.DriveHistoryRepository;
import io.dymatics.cogny.domain.persist.DriveRepairLogRepository;
import io.dymatics.cogny.domain.persist.DtcRawRepository;
import io.dymatics.cogny.domain.persist.RepairMsgRepository;
import io.dymatics.cogny.domain.persist.SensingRawRepository;

@Service
@Transactional(readOnly = true)
public class DriveServiceImpl implements DriveService {
    @Autowired private DriveHistoryRepository driveHistoryRepository;
    @Autowired private SensingRawRepository sensingRawRepository;
    @Autowired private DtcRawRepository dtcRawRepository;
    @Autowired private RepairMsgRepository repairMsgRepository;
    @Autowired private DriveRepairLogRepository driveRepairLogRepository;
    @Autowired private Datastore datastore;
    @Autowired private KeyFactory keyFactory;
    @Autowired @Qualifier("dsExecutor") private TaskExecutor taskExecutor;

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public Object saveStart(DriveHistory driveHistory) {
        finalizeLastDriveHistory(driveHistory);
        driveHistoryRepository.insert(driveHistory);
        return driveHistory;
    }

    private void finalizeLastDriveHistory(DriveHistory driveHistory) {
        DriveHistory lastHistory = driveHistoryRepository.findLastOne(driveHistory.getVehicleNo(), driveHistory.getObdDeviceNo(), driveHistory.getUserNo());
        if (lastHistory != null) {
            SensingRaw lastSensing = sensingRawRepository.findLastOneByDriveHistory(lastHistory.getDriveHistoryNo());
            if (lastSensing != null) {
                lastHistory.setEndTime(lastSensing.getSensingTime());
                lastHistory.setEndMileage(lastSensing.getOdometer());
                int driveDistance = lastSensing.getOdometer() - lastHistory.getStartMileage();
                if (driveDistance < 0) {
                    driveDistance = 0;
                }
                lastHistory.setDriveDistance(driveDistance);
                driveHistoryRepository.update(lastHistory);
            }
        }
    }

    private FastDateFormat DF = FastDateFormat.getInstance("yyyyMMdd", TimeZone.getDefault(), Locale.getDefault());

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void saveEnd(DriveHistory driveHistory) {
        int driveDistance = driveHistory.getEndMileage() - driveHistory.getStartMileage();
        if (driveDistance < 0) {
            driveDistance = 0;
        }
        driveHistory.setDriveDistance(driveDistance);
        driveHistoryRepository.update(driveHistory);
    }

    @Override
    @Transactional(readOnly = false, noRollbackFor = Exception.class)
    public void saveSensings(Sensings sensings) {
        List<SensingLog> sensingLogs = sensings.getSensingLogs();
        if (!sensingLogs.isEmpty()) {
            SensingLog first = sensingLogs.get(0);
            DriveHistory driveHistory = driveHistoryRepository.findByNo(first.getDriveHistoryNo());

            storeSensingRaws(driveHistory, sensingLogs);
            saveSensingRaws(driveHistory, sensingLogs);
            saveDtcRaws(sensings.getDtcRaws());
        }
    }

    private static final FastDateFormat KEY_STAMP = FastDateFormat.getInstance("yyyyMMddHHmmss", TimeZone.getDefault(), Locale.getDefault());
    private void storeSensingRaws(DriveHistory driveHistory, List<SensingLog> sensingLogs) {
        if (driveHistory != null) {
            List<Entity> entities = new ArrayList<>();
            for (SensingLog sensingLog : sensingLogs) {
                SensingRaw sensingRaw = new SensingRaw(driveHistory.getVehicleNo(), driveHistory.getObdDeviceNo(), driveHistory.getDriveHistoryNo(), sensingLog.getValues());
                String values = sensingRaw.toValues();
                if (values != null) {
                    String name = driveHistory.getVehicleNo() + "-" + driveHistory.getDriveHistoryNo() + "-" + KEY_STAMP.format(sensingRaw.getSensingTime()) + "-" + sensingRaw.getSensingSeq();
                    Builder builder = Entity.newBuilder(keyFactory.newKey(name))
                            .set(SensingRaw.KEY_REG_DATE, KEY_STAMP.format(new Date()))
                            .set(SensingRaw.KEY_VALUES, values);
                    Entity entity = builder.build();
                    entities.add(entity);
                }
            }

            if (!entities.isEmpty()) {
                taskExecutor.execute(() -> putToDatastore(entities));
            }
        }
    }

    private void putToDatastore(List<Entity> entities) {
        try {
            if (entities.size() > 100) {
                for (List<Entity> partition : Lists.partition(entities, 100)) {
                    datastore.put(partition.toArray(new Entity[partition.size()]));
                }
            } else {
                datastore.put(entities.toArray(new Entity[entities.size()]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveSensingRaws(DriveHistory driveHistory, List<SensingLog> sensingLogs) {
        if (driveHistory != null) {
            for (SensingLog sensingLog : sensingLogs) {
                SensingRaw sensingRaw = new SensingRaw(driveHistory.getVehicleNo(), driveHistory.getObdDeviceNo(), driveHistory.getDriveHistoryNo(), sensingLog.getValues());
                if (!existSensingRaw(sensingRaw)) {
                    sensingRawRepository.insert(sensingRaw);
                }
            }
        }
    }

    private boolean existSensingRaw(SensingRaw sensingRaw) {
        return sensingRawRepository.countOfDriveHistoryAndSensingSeq(sensingRaw) > 0L;
    }

    private void saveDtcRaws(List<DtcRaw> dtcRaws) {
        if (!dtcRaws.isEmpty()) {
            for (DtcRaw dtcRaw : dtcRaws) {
                DtcRaw find = dtcRawRepository.findByDriveHistoryAndSeq(dtcRaw);
                if (find == null) {
                    dtcRawRepository.insert(dtcRaw);
                } else {
                    find.setDtcUpdatedTime(dtcRaw.getDtcUpdatedTime());
                    dtcRawRepository.update(find);
                }
            }
        }
    }

    @Override
    public Object repairMessage(Long vehicleNo) {
        return repairMsgRepository.findLatestOneByVehicle(vehicleNo);
    }

    private static final Comparator<Entry<Integer, List<DriveRepairLog>>> COMPARATOR_DRIVE_REPAIR_LOG = Comparator.comparing(Map.Entry::getKey);

    @Override
    public DriveRepairLog.Page populateHistories(DriveRepairLog.Page page) {
        manipulatePageParams(page);

        List<DriveRepairLog> list = driveRepairLogRepository.findByPage(page);
        Map<Integer, List<DriveRepairLog>> groups = list.stream().collect(Collectors.groupingBy(DriveRepairLog::getDateIdx));

        page.setContents(groups.entrySet().stream().sorted(COMPARATOR_DRIVE_REPAIR_LOG.reversed()).map(e -> new DriveRepairLog.Summary(e)).collect(Collectors.toList()));

        DriveRepairLog.Summary last = page.getLast();
        if (last != null) {
            Integer lastDateIdx = driveRepairLogRepository.findLastDateIdxByPage(page);
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

    private DriveRepairLog.Page manipulatePageParams(DriveRepairLog.Page page) {
        int pageNo = page.getPage();
        int scale = page.getScale();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -(scale * pageNo));
        page.param("B", DF.format(calendar));

        calendar.add(Calendar.DAY_OF_MONTH, -(scale - 1));
        page.param("A", DF.format(calendar));

        return page;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    @Transactional(readOnly = false, noRollbackFor = Exception.class)
    @Deprecated
    public void saveSensingRawOld(List<SensingLog> sensingLogs) {
        if (!sensingLogs.isEmpty()) {
            SensingLog first = sensingLogs.get(0);
            DriveHistory driveHistory = driveHistoryRepository.findByNo(first.getDriveHistoryNo());
            saveSensingRaws(driveHistory, sensingLogs);
        }
    }

    @Override
    @Transactional(readOnly = false, noRollbackFor = Exception.class)
    @Deprecated
    public void saveDtcRawsOld(List<DtcRaw> dtcRaws) {
        for (DtcRaw dtcRaw : dtcRaws) {
            dtcRawRepository.insert(dtcRaw);
        }
    }

}
