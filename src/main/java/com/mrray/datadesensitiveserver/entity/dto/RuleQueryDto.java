package com.mrray.datadesensitiveserver.entity.dto;

public class RuleQueryDto extends PageQueryDto {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
