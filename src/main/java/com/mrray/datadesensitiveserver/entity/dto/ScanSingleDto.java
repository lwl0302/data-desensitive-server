package com.mrray.datadesensitiveserver.entity.dto;

public class ScanSingleDto {
    private String tableName;
    private String extractId;

    public String getExtractId() {
        return extractId;
    }

    public void setExtractId(String extractId) {
        this.extractId = extractId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
