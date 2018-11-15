package com.mrray.datadesensitiveserver.entity.vo;

public class TaskVo {
    private String taskId;//任务id
    private Integer mediumType;//数据库类型
    private String accessTaskName;//介入任务名称
    private String dataSetId;//数据集id
    private int status;
    private String sinkCatalog;//目标目录

    public String getTaskId() {
        return taskId;
    }

    public TaskVo setTaskId(String taskId) {
        this.taskId = taskId;
        return this;
    }

    public Integer getMediumType() {
        return mediumType;
    }

    public TaskVo setMediumType(Integer mediumType) {
        this.mediumType = mediumType;
        return this;
    }

    public String getAccessTaskName() {
        return accessTaskName;
    }

    public TaskVo setAccessTaskName(String accessTaskName) {
        this.accessTaskName = accessTaskName;
        return this;
    }

    public String getDataSetId() {
        return dataSetId;
    }

    public TaskVo setDataSetId(String dataSetId) {
        this.dataSetId = dataSetId;
        return this;
    }

    public int getStatus() {
        return status;
    }

    public TaskVo setStatus(int status) {
        this.status = status;
        return this;
    }

    public String getSinkCatalog() {
        return sinkCatalog;
    }

    public TaskVo setSinkCatalog(String sinkCatalog) {
        this.sinkCatalog = sinkCatalog;
        return this;
    }
}
