package io.dymatics.cogny.domain.persist;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import io.dymatics.cogny.domain.model.Meta;

@Mapper
public interface MetaRepository {

    @Select("SELECT * FROM `meta` ORDER BY `meta_no` DESC LIMIT 1")
    Meta findLatestOne();

}
