package com.mrray.datadesensitiveserver.entity.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PageQueryVo<T> {

    /**
     * 当前页
     */
    private int page;

    /**
     * 分页大小
     */
    private int size;

    /**
     * 前一页
     */
    private int prevPage;

    /**
     * 是不是第一页
     */
    private boolean isFirstPage;

    /**
     * 后一页
     */
    private int nextPage;

    /**
     * 是不是最后一页
     */
    private boolean isLastPage;

    /**
     * 当前页拥有的元素
     */
    private int currentPageElements;

    /**
     * 总页数
     */
    private int totalPage;

    /**
     * 总的条数
     */
    private long totalElements;

    /**
     * 排序的属性
     */
    private String property;

    /**
     * 排序的方向
     */
    private String direction;

    /**
     * 查询的结果数据
     */
    private List<T> content = new ArrayList<>();

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

    public int getPrevPage() {
        return prevPage;
    }

    public void setPrevPage(int prevPage) {
        this.prevPage = prevPage;
    }

    public boolean isFirstPage() {
        return isFirstPage;
    }

    public void setFirstPage(boolean firstPage) {
        isFirstPage = firstPage;
    }

    public int getNextPage() {
        return nextPage;
    }

    public void setNextPage(int nextPage) {
        this.nextPage = nextPage;
    }

    public boolean isLastPage() {
        return isLastPage;
    }

    public void setLastPage(boolean lastPage) {
        isLastPage = lastPage;
    }

    public int getCurrentPageElements() {
        return currentPageElements;
    }

    public void setCurrentPageElements(int currentPageElements) {
        this.currentPageElements = currentPageElements;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
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
        this.direction = direction;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }
}