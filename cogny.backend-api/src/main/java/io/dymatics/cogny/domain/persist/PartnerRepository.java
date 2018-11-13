package io.dymatics.cogny.domain.persist;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import io.dymatics.cogny.domain.model.Partner;

@Mapper
public interface PartnerRepository {

    @Select("SELECT * FROM `partner` WHERE `partner_code` = #{value }")
    Partner findByCode(String code);

    @Select("SELECT p.`partner_no`, p.`company_name`, COUNT(v.`vehicle_no`) AS `vehicle_count` FROM partner p LEFT JOIN vehicle v ON p.`partner_no` = v.`partner_no` WHERE p.`partner_no` = #{value }")
    Partner findBy(Long partnerNo);
}
