package com.project.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sherl on 2017/12/14.
 */
public class ViewObject {
    private Map<String,Object> obs = new HashMap<>();

    public Object get(String key) {
        return obs.get(key);
    }

    public void setObs(String key,Object value) {
       obs.put(key,value);
    }
}
