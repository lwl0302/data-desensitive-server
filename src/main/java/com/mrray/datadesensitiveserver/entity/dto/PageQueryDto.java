package com.mrray.datadesensitiveserver.entity.dto;

import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;

public class PageQueryDto {

    /**
     * 页码
     */
    @Min(value = 1, message = "PAGE_LT_ONE")
    private int page = 1;

    /**
     * 分页大小
     */
    @Range(min = 3, max = 30, message = "SIZE_NOTIN_RANGE")
    private int size = 10;

    /**
     * 排序属性
     */
    private String property = "id";

    /**
     * 排序方向
     */
    private String direction = "DESC";

    public PageQueryDto() {

    }

    public PageQueryDto(int page, int size) {
        this.page = page;
        this.size = size;
    }

    public PageQueryDto(int page, int size, String property, String direction) {
        this.page = page;
        this.size = size;
        this.property = property;
        this.direction = direction;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = "ASC".equalsIgnoreCase(direction) ? "ASC" : "DESC";
    }

}