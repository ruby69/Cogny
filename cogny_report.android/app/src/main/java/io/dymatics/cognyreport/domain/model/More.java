package io.dymatics.cognyreport.domain.model;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class More<T> implements Serializable {
    private static final long serialVersionUID = 8746457259679686465L;

    private Long scale = 15L;
    private List<T> content;
    private boolean hasMore;

    public T getLast() {
        if(hasContents()) {
            return content.get(content.size() - 1);
        }
        return null;
    }

    private boolean hasContents() {
        return content != null && !content.isEmpty();
    }
}
