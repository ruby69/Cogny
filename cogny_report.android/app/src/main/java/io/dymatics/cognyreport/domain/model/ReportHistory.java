package io.dymatics.cognyreport.domain.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class ReportHistory implements Serializable {
    private static final long serialVersionUID = 8314001167401834591L;
    private String issuedDate;
    private String title;
    private Date time1;
    private Date time2;

    @Data
    @JsonInclude(Include.NON_NULL)
    public static class Group implements Serializable {
        private static final long serialVersionUID = 8006222342280390025L;
        private String issuedDate;
        private List<ReportHistory> dtcList;
        private List<ReportHistory> repairs;
        private List<ReportHistory> messages;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Groups extends ArrayList<Group> {
        private static final long serialVersionUID = -4193142096137018925L;
    }
}
