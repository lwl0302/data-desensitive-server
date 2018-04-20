package com.mrray.datadesensitiveserver.repository;

import com.mrray.datadesensitiveserver.entity.domain.Mode;

public interface ModeRepository extends BaseRepository<Mode> {
    Mode findByUuid(String uuid);

    Mode findByMethodNameAndAlgorithm_Uuid(String methodName, String uuid);
}