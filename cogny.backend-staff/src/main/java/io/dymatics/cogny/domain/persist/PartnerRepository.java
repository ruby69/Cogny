package io.dymatics.cogny.domain.persist;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import io.dymatics.cogny.domain.model.Page;
import io.dymatics.cogny.domain.model.Partner;

@Mapper
public interface PartnerRepository {

    @Insert("INSERT INTO `partner` (`partner_code`, `company_name`, `partner_type`, `tel`, `postcode_no`, `addr_post_code`, `addr_detail`, `person_in_charge`, `contract_status`, `enabled`) values(#{partnerCode}, #{companyName}, #{partnerType}, #{tel}, #{postcodeNo}, #{addrPostCode}, #{addrDetail}, #{personInCharge}, #{contractStatus}, true)")
    @Options(useGeneratedKeys = true, keyProperty = "partnerNo", keyColumn = "partner_no")
    void insert(Partner partner);

    Partner findByNo(Long partnerNo);

    int countByPage(Page page);

    List<Partner> findByPage(Page page);

    void update(Partner partner);

    void delete(Long partnerNo);

    @Select("SELECT `partner_code` FROM `partner`")
    Set<String> findAllPartnerCodes();

}
