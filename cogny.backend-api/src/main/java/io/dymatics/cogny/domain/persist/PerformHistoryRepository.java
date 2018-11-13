package io.dymatics.cogny.domain.persist;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import io.dymatics.cogny.domain.model.PerformHistory;

@Mapper
public interface PerformHistoryRepository {

    void insert(PerformHistory performHistory);

    @Select("SELECT * " +
            "FROM `perform_history` " +
            "WHERE" +
            "  `vehicle_no` = #{p.vehicleNo}" +
            "  AND `date_idx` BETWEEN #{p.A} AND #{p.B} " +
            "ORDER BY `date_idx` DESC, `issued_time` DESC, `perform_history_no` DESC")
    List<PerformHistory> findByPage(PerformHistory.Page page);

    @Select("SELECT MIN(`date_idx`) FROM `perform_history` WHERE `vehicle_no` = #{p.vehicleNo}")
    Integer findLastDateIdxByPage(PerformHistory.Page page);

}
