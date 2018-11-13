package io.dymatics.cogny.domain.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class Component implements Serializable {
    private static final long serialVersionUID = -5071150789094280820L;
    
    private Long componentNo;
    private Long manufacturerNo;
    private String name;

    private Long componentCateNo;
    private Long componentCate1No;
    private Long componentCate2No;
    
    private ComponentCate1 componentCate1;
    private ComponentCate2 componentCate2;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(Include.NON_NULL)
    public static class ComponentCate1 implements Serializable {
        private static final long serialVersionUID = -8770037651275487109L;
        
        private Long componentCate1No;
        private Integer code;
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(Include.NON_NULL)
    public static class ComponentCate2 implements Serializable {
        private static final long serialVersionUID = -7974911239168979613L;
        
        private Long componentCate2No;
        private Long componentCate1No;
        private Integer code;
        private String name;
    }

    @Data
    @JsonInclude(Include.NON_NULL)
    public static class ComponentForm implements Serializable {
        private static final long serialVersionUID = 9027989876655731322L;

        private Integer manufacturerNo;
        private Integer componentCate1No;
        private Integer componentCate2No;
    }
}
