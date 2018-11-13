package io.dymatics.cogny.domain.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.dymatics.cogny.domain.model.Component.ComponentCate1;
import io.dymatics.cogny.domain.model.Component.ComponentCate2;
import io.dymatics.cogny.domain.model.EnumToMap.EnumValuable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class Repair implements Serializable {
    private static final long serialVersionUID = -2616846048204161179L;

    private Long repairNo;
    private Long vehicleNo;
    private Long repairShopNo;
    private Long odometer;
    private Date repairDate;
    private String memo;


    @JsonIgnore private Boolean enabled;
    private Date regDate;
    private Date updDate;
    @JsonIgnore private Date delDate;

    private Vehicle vehicle;
    private List<RepairComponent> repairComponentList;

    public enum RepairCategory implements EnumValuable {
        SUPPLEMENT("보충"), DEMOUNT("탈착"), REPLACE("교환"), DISASSEMBLE("분해"), CHECK("점검"), ALIGN("조정"), REPAIR("수리"), ETC("기타");
        private final String name;

        private RepairCategory(String name) {
            this.name = name;
        }
        @Override
        public String getName() {
            return name;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(Include.NON_NULL)
    public static class RepairComponent implements Serializable {
        private static final long serialVersionUID = -1149494146057032864L;

        private Long repairComponentNo;

        private Long repairNo;
        private Long componentCateNo;
        private RepairCategory category;
        private String categoryName;
        private Long cost;
        private String memo;

        private Boolean enabled;
        private Date regDate;
        private Date updDate;
        @JsonIgnore private Date delDate;

        private Component component;
        private ComponentCate1 componentCate1;
        private ComponentCate2 componentCate2;


        public void setCategory(RepairCategory category) {
            this.category = category;
            this.categoryName = category.getName();
        }

    }

    @Data
    @JsonInclude(Include.NON_NULL)
    public static class Form implements Serializable {
        private static final long serialVersionUID = 4387915087212456094L;

        private Long repairNo;
        private Long vehicleNo;
        private Long repairShopNo;
        private Long odometer;
        private Date repairDate;
        private String memo;

        private List<RepairComponent> repairComponentList;
    }
}
