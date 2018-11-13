package io.dymatics.cogny.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public interface EnumToMap {

    interface EnumValuable {
        String getName();
    }

    static List<Map<String, Object>> getEnumList(EnumValuable[] values) {
        List<Map<String, Object>> emumStatusList = new ArrayList<Map<String, Object>>();
        for (EnumValuable value : values) {
            Map<String, Object> enumtoMap = new TreeMap<String, Object>();
            enumtoMap.put("value", value);
            enumtoMap.put("text", value.getName());
            emumStatusList.add(enumtoMap);
        }
        return emumStatusList;
    }
}
