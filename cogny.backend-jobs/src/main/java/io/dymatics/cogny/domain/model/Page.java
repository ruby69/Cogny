package io.dymatics.cogny.domain.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class Page implements Serializable {
    private static final long serialVersionUID = -6393774940845746189L;

    private int page = 1;
    private int scale = 15;
    private int total = 0;
    private List<?> contents;
    
    private String query;
    private Long partnerNo;
    private Long vehicleNo;
    private String vehicleStatus;
    @JsonIgnore private User currentUser;
    @JsonIgnore private Long authorizedPartnerNo;

    public int getBeginIndex(){
        return (page - 1) * scale;
    }

    public int getTotalPages(){
        if(total == scale) {
            return 1;
        } else if(total % scale == 0) {
            return total / scale;
        } else {
            return (total / scale) + 1;
        }
    }

    public void setPage(Integer page){
        this.page = page == null || page.intValue() < 1 ? 1 : page.intValue();
    }

    public void setTotal(int total) {
        this.total = total;
        int totalPages = getTotalPages();
        if (totalPages > 0 && page > totalPages) {
            page = totalPages;
        }
    }

    public boolean isHasNext() {
        return page < getTotalPages();
    }

    public boolean isHasPrevious() {
        return page > 1;
    }

//    public Page clear() {
////        p.clear();
//        return this;
//    }
}
