package com.mrray.datadesensitiveserver.repository;

import com.mrray.datadesensitiveserver.entity.domain.Algorithm;

import java.util.List;

public interface AlgorithmRepository extends BaseRepository<Algorithm> {
    List<Algorithm> findAllByOriginalTrueOrderByPriorityAsc();

    List<Algorithm> findAllByOriginalFalse();

    Algorithm findByClassName(String className);

    Algorithm findByUuid(String uuid);
}