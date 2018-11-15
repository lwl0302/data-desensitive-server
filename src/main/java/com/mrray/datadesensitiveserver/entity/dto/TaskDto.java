package com.mrray.datadesensitiveserver.entity.dto;

import java.util.List;

public class TaskDto {
    private String taskId;
    private String dataSetId;
    private String mediumType;
    private List<String> modes;
    private String accessTaskName;

    public String getAccessTaskName() {
        return accessTaskName;
    }

    public TaskDto setAccessTaskName(String accessTaskName) {
        this.accessTaskName = accessTaskName;
        return this;
    }

    public String getTaskId() {
        return taskId;
    }

    public TaskDto setTaskId(String taskId) {
        this.taskId = taskId;
        return this;
    }

    public String getDataSetId() {
        return dataSetId;
    }

    public TaskDto setDataSetId(String dataSetId) {
        this.dataSetId = dataSetId;
        return this;
    }

    public String getMediumType() {
        return mediumType;
    }

    public TaskDto setMediumType(String mediumType) {
        this.mediumType = mediumType;
        return this;
    }

    public List<String> getModes() {
        return modes;
    }

    public TaskDto setModes(List<String> modes) {
        this.modes = modes;
        return this;
    }
}
