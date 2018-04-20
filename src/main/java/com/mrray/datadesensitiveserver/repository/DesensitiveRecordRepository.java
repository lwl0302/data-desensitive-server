package com.mrray.datadesensitiveserver.repository;

import com.mrray.datadesensitiveserver.entity.domain.DesensitiveRecord;
import org.springframework.data.jpa.repository.Query;

public interface DesensitiveRecordRepository extends BaseRepository<DesensitiveRecord> {
    DesensitiveRecord findByUuid(String uuid);

    DesensitiveRecord findByNewTableName(String newTableName);

    @Query("select sum(counts) from DesensitiveRecord")
    Long sumCounts();
}