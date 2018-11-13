package io.dymatics.cogny.domain.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class Vehicle implements Serializable {
    private static final long serialVersionUID = -4661240245741065874L;

    private Long vehicleNo;
    private Long modelNo;
    private Long fuelNo;
    private String licenseNo;
    private int modelYear;
    private int modelMonth;
    @JsonIgnore private boolean enabled;
    private String regDate;
    @JsonIgnore private String updDate;
    @JsonIgnore private String delDate;

    private String modelName;
    private String fuelName;
    private Long manufacturerNo;
    private String manufacturerName;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(Include.NON_NULL)
    public static class Fuel implements Serializable {
        private static final long serialVersionUID = 8876882408515520065L;

        private Long fuelNo;
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(Include.NON_NULL)
    public static class Manufacturer implements Serializable {
        private static final long serialVersionUID = 1265848100343759190L;

        private Long manufacturerNo;
        private String name;
        private List<ModelGroup> ModelGroups;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(Include.NON_NULL)
    public static class ModelGroup implements Serializable {
        private static final long serialVersionUID = -1571680777738064573L;

        public enum Type {
            SEDAN, SUV, VAN, BUS, TRUCK, ETC
        }

        private Long modelGroupNo;
        private Long manufacturerNo;
        private String name;
        private Type type;

        private Manufacturer manufacturer;
        private List<Model> models;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(Include.NON_NULL)
    public static class Model implements Serializable {
        private static final long serialVersionUID = -4800814977144673691L;

        private Long modelNo;
        private Long modelGroupNo;
        private String name;
        private ModelGroup modelGroup;
    }

    @Data
    @NoArgsConstructor
    @JsonInclude(Include.NON_NULL)
    public static class UserVehicle implements Serializable {
        private static final long serialVersionUID = 6202689391615908295L;

        private Long userVehicleNo;
        private Long userNo;
        private Long vehicleNo;
    }

    @Data
    @JsonInclude(Include.NON_NULL)
    public static class UserVehicleForm implements Serializable {
        private static final long serialVersionUID = -158757344975744270L;

        private Long userVehicleNo;
        private Long vehicleNo;
        private Long modelNo;
        private Long fuelNo;
        private String licenseNo;
        private int modelYear;
        private int modelMonth;

        public Vehicle getVehicle() {
            Vehicle vehicle = new Vehicle();
            vehicle.setVehicleNo(vehicleNo);
            vehicle.setModelNo(modelNo);
            vehicle.setFuelNo(fuelNo);
            vehicle.setLicenseNo(licenseNo);
            vehicle.setModelYear(modelYear);
            vehicle.setModelMonth(modelMonth);
            return vehicle;
        }
    }

}
