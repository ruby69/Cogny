package io.dymatics.cogny.jobs.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery;
import com.google.cloud.datastore.StructuredQuery.CompositeFilter;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;

import io.dymatics.cogny.domain.model.DriveHistory;
import io.dymatics.cogny.domain.model.SensingRaw;
import io.dymatics.cogny.jobs.service.SensingRawService;

@Service
@Transactional(readOnly = true)
public class SensingRawServiceImpl implements SensingRawService {
    @Autowired private Datastore datastore;
    
    @Value("${datastore.kind-key}") private String kindKey;

    @Override
    public List<SensingRaw> getSensingRawDatastore(DriveHistory driveHistory) throws Exception {
        String startName = String.valueOf(driveHistory.getVehicleNo()) + "-" + String.valueOf(driveHistory.getDriveHistoryNo()) + "-0000";
        String endName =   String.valueOf(driveHistory.getVehicleNo()) + "-" + String.valueOf(driveHistory.getDriveHistoryNo()) + "-9999";

        Key startNamespace = datastore.newKeyFactory().setKind(kindKey).newKey(startName);
        Key endNamespace = datastore.newKeyFactory().setKind(kindKey).newKey(endName);

        CompositeFilter filter = StructuredQuery.CompositeFilter.and(
                PropertyFilter.gt("__key__", startNamespace),
                PropertyFilter.lt("__key__", endNamespace)
                );
        
        Query<Entity> query = Query.newEntityQueryBuilder()
                .setKind(kindKey)
                .setFilter(filter)
                .build();

        QueryResults<Entity> entities = datastore.run(query);
        List<SensingRaw> sensingRawList = new ArrayList<SensingRaw>();
        while(entities.hasNext()) {
            Entity sensingRawEntity = entities.next();
            SensingRaw sensingRaw = new SensingRaw(sensingRawEntity);
            sensingRawList.add(sensingRaw);
        }
        return sensingRawList;
    }

}
