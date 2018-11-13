package io.dymatics.cogny.domain.model;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.dymatics.cogny.domain.model.EnumToMap.EnumValuable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class Partner implements Serializable {
    private static final long serialVersionUID = -5288969724139922653L;

    public enum PartnerType implements EnumValuable {
        TAXI("택시"), RENTAL("렌터카");
        private final String name;

        private PartnerType(String name) {
            this.name = name;
        }
        @Override
        public String getName() {
            return name;
        }
    }

    public enum ContractStatus implements EnumValuable {
        STANDBY("대기"), CONTRACTED("계약중"), NONCONTRACT("미계약");
        private final String name;

        private ContractStatus(String name) {
            this.name = name;
        }
        @Override
        public String getName() {
            return name;
        }
    }

    private Long partnerNo;
    private String partnerCode;
    private String companyName;
    private PartnerType partnerType;
    private String partnerTypeName;
    private String tel;
    private Long postcodeNo;
    private String postcode;
    private String addrPostCode;
    private String addrDetail;
    private String personInCharge;
    private ContractStatus contractStatus;
    private String contractStatusName;
    private Boolean enabled;
    private Date regDate;
    private Date updDate;
    private Date delDate;

    public Partner(Long partnerNo, String companyName, PartnerType partnerType, String tel, Long postcodeNo, String addrPostCode, String addrDetail, String personInCharge, ContractStatus contractStatus) {
        this.partnerNo = partnerNo;
        this.companyName = companyName;
        this.partnerType = partnerType;
        this.partnerTypeName = partnerType.getName();
        this.tel = tel;
        this.postcodeNo = postcodeNo;
        this.addrPostCode = addrPostCode;
        this.addrDetail = addrDetail;
        this.personInCharge = personInCharge;
        this.contractStatus = contractStatus;
        this.contractStatusName = contractStatus.getName();
    }

    public Partner(String partnerCode, String companyName, PartnerType partnerType, String tel, Long postcodeNo, String addrPostCode, String addrDetail, String personInCharge, ContractStatus contractStatus) {
        this.partnerCode = partnerCode;
        this.companyName = companyName;
        this.partnerType = partnerType;
        this.partnerTypeName = partnerType.getName();
        this.tel = tel;
        this.postcodeNo = postcodeNo;
        this.addrPostCode = addrPostCode;
        this.addrDetail = addrDetail;
        this.personInCharge = personInCharge;
        this.contractStatus = contractStatus;
        this.contractStatusName = contractStatus.getName();
    }

    public void setPartnerType(PartnerType partnerType) {
        this.partnerType = partnerType;
        this.partnerTypeName = partnerType.getName();
    }

    public void setContractStatus(ContractStatus contractStatus) {
        this.contractStatus = contractStatus;
        this.contractStatusName = contractStatus.getName();
    }

    @Data
    @JsonInclude(Include.NON_NULL)
    public static class Form implements Serializable {
        private static final long serialVersionUID = 1117505890750708370L;

        private Long partnerNo;
        private String companyName;
        private PartnerType partnerType;
        private String tel;
        private Long postcodeNo;
        private String addrPostCode;
        private String addrDetail;
        private String personInCharge;
        private ContractStatus contractStatus;

        public Partner getPartner() {
            return new Partner(partnerNo, companyName, partnerType, tel, postcodeNo, addrPostCode, addrDetail, personInCharge, contractStatus);
        }
    }
}
