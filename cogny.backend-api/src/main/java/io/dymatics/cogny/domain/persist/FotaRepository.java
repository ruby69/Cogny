package io.dymatics.cogny.domain.persist;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import io.dymatics.cogny.domain.model.Fota;

@Mapper
public interface FotaRepository {

    @Select("SELECT * FROM `fota` WHERE `enabled` = 1 AND `type` = #{value } ORDER BY `fota_no` DESC LIMIT 1")
    Fota findLatestByType(String type);

}
