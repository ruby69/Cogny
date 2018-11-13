package io.dymatics.cogny.staff.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Entity.Builder;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery;
import com.google.cloud.datastore.StructuredQuery.CompositeFilter;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;

import io.dymatics.cogny.domain.model.DriveHistory;
import io.dymatics.cogny.domain.model.SensingRaw;
import io.dymatics.cogny.domain.model.SensingRaw.SendingRawDataSet;
import io.dymatics.cogny.domain.persist.DriveHistoryRepository;
import io.dymatics.cogny.domain.persist.DtcRepository;
import io.dymatics.cogny.domain.persist.SensingRawRepository;
import io.dymatics.cogny.staff.service.SensingRawService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional(readOnly = true)
public class SensingRawServiceImpl implements SensingRawService{
    @Autowired private SensingRawRepository sensorDataRepository;
    @Autowired private DtcRepository dtcRepository;
    @Autowired private DriveHistoryRepository driveHistoryRepository;
    
    @Autowired private Datastore datastore;
    
//    private static final String KIND = "sensing_raw_product";
    @Value("${datastore.kind-key}") private String kindKey;


    @Override
    public Object getSensingRaw(SensingRaw.Form form) throws Exception {
        List<SensingRaw> sensingRawList = this.getSensingRawDatastore(form);
        if(sensingRawList.size() == 0) {
            sensingRawList = sensorDataRepository.findByPage(form);
            form.getPage().setTotal(sensorDataRepository.countByPage(form));
        }
        return new SendingRawDataSet(form.getFieldList(), 
                                    sensingRawList,
                                    dtcRepository.findByDriveHistoryNo(form.getDriveHistoryNo()), 
                                    form.getPage());
//        return new SendingRawDataSet(form.getFieldList(), 
//                sensorDataRepository.findByPage(form), 
//                dtcRepository.findByDriveHistoryNo(form.getDriveHistoryNo()), 
//                form.getPage());

    }

    @Override
    public List<SensingRaw> getSensingRawDatastore(SensingRaw.Form form) throws Exception {
        String startName = String.valueOf(form.getVehicleNo()) + "-" + String.valueOf(form.getDriveHistoryNo()) + "-0000";
        String endName =   String.valueOf(form.getVehicleNo()) + "-" + String.valueOf(form.getDriveHistoryNo()) + "-9999";

        Key startNamespace = datastore.newKeyFactory().setKind(kindKey).newKey(startName);
        Key endNamespace = datastore.newKeyFactory().setKind(kindKey).newKey(endName);

        CompositeFilter filter = StructuredQuery.CompositeFilter.and(
                PropertyFilter.gt("__key__", startNamespace),
                PropertyFilter.lt("__key__", endNamespace)
                );
//        Query<Key> keyQuery = Query.newKeyQueryBuilder()
//                .setKind(kindKey)
//                .setFilter(filter)
//                .build();
        
//        QueryResults<Key> keys = datastore.run(keyQuery);
//        int total = 0;
//        while(keys.hasNext()) {
//            keys.next();
//            total++;
//        }
//        form.getPage().setTotal(total);

        Query<Entity> query = Query.newEntityQueryBuilder()
                .setKind(kindKey)
                .setFilter(filter)
                .setOffset(form.getPage().getBeginIndex())
                .setLimit(form.getPage().getScale() + 1)
                .build();
        
        QueryResults<Entity> entities = datastore.run(query);
        List<SensingRaw> sensingRawList = new ArrayList<SensingRaw>();
        while(entities.hasNext()) {
            Entity sensingRawEntity = entities.next();
            SensingRaw sensingRaw = new SensingRaw(sensingRawEntity);
            sensingRawList.add(sensingRaw);
        }

        if(form.getPage().getTotal() < form.getPage().getBeginIndex() + sensingRawList.size()) {
            form.getPage().setTotal(form.getPage().getBeginIndex() + sensingRawList.size());
        }
        return sensingRawList;
    }
    
    @Override
    public void migrationSensingRaw(Long startDriveHistoryNo, Long endDriveHistoryNo) {
        log.info("kind-key : " + kindKey );
        List<DriveHistory> driveHistoryList = driveHistoryRepository.findAll(startDriveHistoryNo, endDriveHistoryNo);
        for(DriveHistory driveHistory: driveHistoryList) {
            log.info("start migration for the drive_history_no : " + driveHistory.getVehicleNo() + "-" + driveHistory.getDriveHistoryNo() + "-");
            putSensingRawDataStore(driveHistory);
        }
    }
    
    private static final FastDateFormat KEY_STAMP= FastDateFormat.getInstance("yyyyMMddHHmmss", TimeZone.getDefault(), Locale.getDefault());
    private void putSensingRawDataStore(DriveHistory driveHistory) {
        List<SensingRaw> sensingRawList = sensorDataRepository.findAllByDriveHistory(driveHistory);
        if(sensingRawList.size() == 0) return;
        try {
            KeyFactory keyFactory = datastore.newKeyFactory().setKind(kindKey);
            List<Entity> entities = new ArrayList<>();
            int insertedRaw = 0;
            for(SensingRaw sensingRaw: sensingRawList) {
                String values = sensingRaw.toValues();
                if (values != null) {
                    String name = driveHistory.getVehicleNo() + "-" + driveHistory.getDriveHistoryNo() + "-" + KEY_STAMP.format(sensingRaw.getSensingTime()) + "-" + sensingRaw.getSensingSeq();
                    Builder builder = Entity.newBuilder(keyFactory.newKey(name))
//                            .set(SensingRaw.KEY_DRIVE_HISTORY_NO, driveHistory.getDriveHistoryNo())
                            .set(SensingRaw.KEY_REG_DATE, sensingRaw.getRegDate().getTime())
                            .set(SensingRaw.KEY_VALUES, values);
                    Entity entity = builder.build();
                    entities.add(entity);
//                    datastore.put(entity);
                    if(entities.size() == 500) {
                        datastore.put(entities.toArray(new Entity[entities.size()]));
                        insertedRaw += entities.size();
                        entities.clear();
                        log.info("inserted raw : " + driveHistory.getVehicleNo() + "-" + driveHistory.getDriveHistoryNo() + "- : " + insertedRaw);
                    }
                    
                }
            }
            datastore.put(entities.toArray(new Entity[entities.size()]));
            insertedRaw += entities.size();
            log.info("inserted raw : " + insertedRaw);
            
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
//    private List<SensingRaw> getSensingRaw(Long vehicleNo, Long driveHistoryNo) {
//        String startName = String.valueOf(vehicleNo) + "-" + String.valueOf(driveHistoryNo) + "-0000";
//        String endName =   String.valueOf(vehicleNo) + "-" + String.valueOf(driveHistoryNo) + "-9999";
//
//        Key startNamespace = datastore.newKeyFactory().setKind(kindKey).newKey(startName);
//        Key endNamespace = datastore.newKeyFactory().setKind(kindKey).newKey(endName);
//
//        CompositeFilter filter = StructuredQuery.CompositeFilter.and(
//                PropertyFilter.gt("__key__", startNamespace),
//                PropertyFilter.lt("__key__", endNamespace)
//                );
//        
//        Query<Entity> query = Query.newEntityQueryBuilder()
//                .setKind(kindKey)
//                .setFilter(filter)
//                .build();
//
//        QueryResults<Entity> entities = datastore.run(query);
//        List<SensingRaw> sensingRawList = new ArrayList();
////        int temp = 0;
//        while(entities.hasNext()) {
////            temp++;
////            if(temp > 100) break;
//            Entity sensingRawEntity = entities.next();
//            SensingRaw sensingRaw = new SensingRaw(sensingRawEntity);
//            sensingRawList.add(sensingRaw);
////            log.info("sensingRaw :" + sensingRaw);
//        }
//        return sensingRawList;
//    }
}
