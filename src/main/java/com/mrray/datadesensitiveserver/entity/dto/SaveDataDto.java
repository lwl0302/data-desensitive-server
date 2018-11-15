package com.mrray.datadesensitiveserver.entity.dto;

import java.util.List;
import java.util.Map;

public class SaveDataDto {
    private String dataSetId;
    private String sinkTableName;
    private String mediumType;
    private List<Map<String, String>> desensitizationData;

    public String getDataSetId() {
        return dataSetId;
    }

    public SaveDataDto setDataSetId(String dataSetId) {
        this.dataSetId = dataSetId;
        return this;
    }

    public String getSinkTableName() {
        return sinkTableName;
    }

    public SaveDataDto setSinkTableName(String sinkTableName) {
        this.sinkTableName = sinkTableName;
        return this;
    }

    public String getMediumType() {
        return mediumType;
    }

    public SaveDataDto setMediumType(String mediumType) {
        this.mediumType = mediumType;
        return this;
    }

    public List<Map<String, String>> getDesensitizationData() {
        return desensitizationData;
    }

    public SaveDataDto setDesensitizationData(List<Map<String, String>> desensitizationData) {
        this.desensitizationData = desensitizationData;
        return this;
    }
}
