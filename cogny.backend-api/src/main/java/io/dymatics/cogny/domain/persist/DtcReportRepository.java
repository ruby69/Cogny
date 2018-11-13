package io.dymatics.cogny.domain.persist;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import io.dymatics.cogny.domain.model.DtcReport;

@Mapper
public interface DtcReportRepository {

    List<DtcReport> findByPartner(Long partnerNo);

}
