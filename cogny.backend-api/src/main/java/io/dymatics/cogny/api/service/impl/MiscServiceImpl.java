package io.dymatics.cogny.api.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.dymatics.cogny.api.service.MiscService;
import io.dymatics.cogny.domain.persist.FotaRepository;
import io.dymatics.cogny.domain.persist.MetaRepository;

@Service
@Transactional(readOnly = true)
public class MiscServiceImpl implements MiscService {
    @Autowired private FotaRepository fotaRepository;
    @Autowired private MetaRepository metaRepository;

    @Override
    public Object findLatestMeta() {
        return metaRepository.findLatestOne();
    }

    @Override
    public Object findLatestFotaBy(String type) {
        return fotaRepository.findLatestByType(type);
    }

}
