package com.mrray.datadesensitiveserver.entity.dto;

import java.util.List;

public class SetColumnsDto {
    private List<ColumnDto> columnDtos;
    private String scanRecord;

    public List<ColumnDto> getColumnDtos() {
        return columnDtos;
    }

    public void setColumnDtos(List<ColumnDto> columnDtos) {
        this.columnDtos = columnDtos;
    }

    public String getScanRecord() {
        return scanRecord;
    }

    public void setScanRecord(String scanRecord) {
        this.scanRecord = scanRecord;
    }
}
