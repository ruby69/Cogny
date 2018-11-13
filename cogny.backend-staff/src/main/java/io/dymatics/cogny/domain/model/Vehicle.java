package io.dymatics.cogny.domain.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class Vehicle implements Serializable {
    private static final long serialVersionUID = 6646770229920300418L;

    private Long vehicleNo;
    private Long modelNo;
    private Long fuelNo;
    private String licenseNo;
    private Integer modelYear;
    private Integer modelMonth;
    private Long currentMileage;
    private Long partnerNo;
    private String memo;
    @JsonIgnore private Boolean enabled;
    private String regDate;
    private String updDate;
    @JsonIgnore private String delDate;
    

    private Model model;
    private Fuel fuel;
    private Partner partner;
    private ObdDevice obdDevice;
    private Long obdDeviceVehicleNo;
    private Date obdInstallDate;
    
    @JsonIgnore private Long regUserNo;

    public Vehicle(Long partnerNo, Long vehicleNo, Long modelNo, Long fuelNo, String licenseNo, int modelYear, int modelMonth, String memo) {
        this(partnerNo, modelNo, fuelNo, licenseNo, modelYear, modelMonth, memo);
        this.vehicleNo = vehicleNo;
    }

    public Vehicle(Long partnerNo, Long modelNo, Long fuelNo, String licenseNo, int modelYear, int modelMonth, String memo) {
        this.partnerNo = partnerNo;
        this.modelNo = modelNo;
        this.fuelNo = fuelNo;
        this.licenseNo = licenseNo;
        this.modelYear = modelYear;
        this.modelMonth = modelMonth;
        this.memo = memo;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(Include.NON_NULL)
    public static class Fuel implements Serializable {
        private static final long serialVersionUID = 7446227967080184761L;

        private Long fuelNo;
        private String name;
        @JsonIgnore private Boolean enabled;
        private Date regDate;
        private Date updDate;
        @JsonIgnore private Date delDate;

        public Fuel(String name) {
            this.name = name;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(Include.NON_NULL)
    public static class Manufacturer implements Serializable {
        private static final long serialVersionUID = 4077185676567004222L;

        private Long manufacturerNo;
        private String name;
        private String country;
        @JsonIgnore private Boolean enabled;
        private Date regDate;
        private Date updDate;
        @JsonIgnore private Date delDate;

        private List<ModelGroup> modelGroups;

        public Manufacturer(String name) {
            this.name = name;
        }
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(Include.NON_NULL)
    public static class ModelGroup implements Serializable {
        private static final long serialVersionUID = -1232051388603261895L;

        public enum Type implements EnumValuable {
            SEDAN("승용차"), SUV("SUV/RV"), VAN("승합차"), BUS("버스"), TRUCK("화물차"), ETC("기타");
            private final String name;

            private Type(String name) {
                this.name = name;
            }

            @Override
            public String getName() {
                return name;
            }
        }

        private Long modelGroupNo;
        private Long manufacturerNo;
        private String name;
        private Type type;
        @JsonIgnore private Boolean enabled;
        private Date regDate;
        private Date updDate;
        @JsonIgnore private Date delDate;

        private Manufacturer manufacturer;
        private List<Model> models;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(Include.NON_NULL)
    public static class Model implements Serializable {
        private static final long serialVersionUID = -6095501801232754623L;

        private Long modelNo;
        private Long modelGroupNo;
        private String name;
        private Integer beginYear;
        private Integer endYear;

        @JsonIgnore private Boolean enabled;
        private Date regDate;
        private Date updDate;
        @JsonIgnore private Date delDate;

        private Manufacturer manufacturer;
        private ModelGroup modelGroup;

        public Model(Long modelGroupNo, String name) {
            this.modelGroupNo = modelGroupNo;
            this.name = name;
        }
    }

    @Data
    @JsonInclude(Include.NON_NULL)
    public static class Form implements Serializable {
        private static final long serialVersionUID = -1569425079677089665L;

        private Long vehicleNo;
        private Long partnerNo;
        private Long modelNo;
        private Long fuelNo;
        private String licenseNo;
        private int modelYear;
        private int modelMonth;
        private String memo;

        public Vehicle getVehicle() {
            return new Vehicle(partnerNo, vehicleNo, modelNo, fuelNo, licenseNo, modelYear, modelMonth, memo);
        }
    }

}
