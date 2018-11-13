package io.dymatics.cogny.domain.persist;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import io.dymatics.cogny.domain.model.PerformHistory;

@Mapper
public interface PerformHistoryRepository {

    void insert(PerformHistory performHistory);

}
