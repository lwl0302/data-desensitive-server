package com.mrray.datadesensitiveserver.entity.vo;

import java.util.List;
import java.util.Map;

public class AlgorithmVo {
    private String name;
    //private String description;
    private Boolean original;
    private String uuid;
    private List<Map<String, Object>> modes;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public List<Map<String, Object>> getModes() {
        return modes;
    }

    public void setModes(List<Map<String, Object>> modes) {
        this.modes = modes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //public String getDescription() {
    //    return description;
    //}
    //
    //public void setDescription(String description) {
    //    this.description = description;
    //}

    public Boolean getOriginal() {
        return original;
    }

    public void setOriginal(Boolean original) {
        this.original = original;
    }
}