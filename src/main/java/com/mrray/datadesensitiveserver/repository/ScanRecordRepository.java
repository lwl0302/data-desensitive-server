package com.mrray.datadesensitiveserver.repository;

import com.mrray.datadesensitiveserver.entity.domain.ScanRecord;

public interface ScanRecordRepository extends BaseRepository<ScanRecord> {
    ScanRecord findByTableName(String tableName);

    ScanRecord findByUuid(String uuid);
}