package com.projmgmt.ucenter.core;

import java.util.HashMap;
import java.util.Map;

public class AjaxResult extends HashMap<String, Object> {
    public static AjaxResult success() {
        AjaxResult r = new AjaxResult();
        r.put("code", 200);
        r.put("message", "success");
        return r;
    }

    public static AjaxResult success(Object data) {
        AjaxResult r = success();
        r.put("data", data);
        return r;
    }

    public static AjaxResult error(String msg) {
        AjaxResult r = new AjaxResult();
        r.put("code", 500);
        r.put("message", msg);
        return r;
    }

    public AjaxResult data(String key, Object value) {
        Map<String, Object> data = (Map<String, Object>) this.get("data");
        if (data == null) {
            data = new HashMap<>();
            this.put("data", data);
        }
        data.put(key, value);
        return this;
    }
}


