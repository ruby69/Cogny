package io.dymatics.cogny.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.ArrayList;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@DatabaseTable(tableName = "Vehicle")
public class Vehicle implements Serializable {
    private static final long serialVersionUID = -1181567038396916304L;

    public static final String FIELD_pk = "vehicleNo";

    @DatabaseField(columnName = FIELD_pk, id = true, generatedId = false) private Long vehicleNo;
    @DatabaseField private Long modelNo;
    @DatabaseField private Long fuelNo;
    @DatabaseField private String licenseNo;
    @DatabaseField private int modelYear;
    @DatabaseField private int modelMonth;
    @DatabaseField private String regDate;

    @DatabaseField private String modelName;
    @DatabaseField private String fuelName;
    @DatabaseField private Long manufacturerNo;
    @DatabaseField private String manufacturerName;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ListWrapper extends ArrayList<Vehicle> {
        private static final long serialVersionUID = 1662768575360744257L;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Fuel implements Serializable {
        private static final long serialVersionUID = 8007327504983475413L;

        private Long fuelNo;
        private String name;

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class ListWrapper extends ArrayList<Fuel> {
            private static final long serialVersionUID = 6318361957765385865L;
        }
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Manufacturer implements Serializable {
        private static final long serialVersionUID = 8338106009823128940L;

        private Long manufacturerNo;
        private String name;

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class ListWrapper extends ArrayList<Manufacturer> {
            private static final long serialVersionUID = 6733222369935723822L;
        }
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Model implements Serializable {
        private static final long serialVersionUID = 5966189250072216116L;

        private Long modelNo;
        private Long manufacturerNo;
        private String name;
        private Manufacturer manufacturer;

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class ListWrapper extends ArrayList<Model> {
            private static final long serialVersionUID = 4153685064632341568L;
        }
    }
}
