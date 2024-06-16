package com.burukeyou.uniapi.support.arg;

import java.util.Map;

public class MapArgList extends ArgList {

    public MapArgList(Map<?,?> map) {
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();
            paramsList.add(new MapParam(key,value));
        }
    }
}
