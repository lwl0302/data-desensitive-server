package com.mrray.datadesensitiveserver.repository;

import com.mrray.datadesensitiveserver.entity.domain.Rule;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RuleRepository extends BaseRepository<Rule> {

    @Transactional
    Integer deleteByUuid(String uuid);

    List<Rule> findByTemporaryFalse();

    Rule findByUuid(String uuid);

    List<Rule> findByName(String name);
}