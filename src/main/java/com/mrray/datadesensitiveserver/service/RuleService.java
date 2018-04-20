package com.mrray.datadesensitiveserver.service;

import com.mrray.datadesensitiveserver.entity.dto.RuleDto;
import com.mrray.datadesensitiveserver.entity.dto.RuleQueryDto;
import com.mrray.datadesensitiveserver.entity.vo.RestResponseBody;

public interface RuleService {
    RestResponseBody getRuleList(RuleQueryDto ruleQueryDto);

    RestResponseBody postRule(RuleDto ruleDto);

    RestResponseBody putRule(String uuid, RuleDto ruleDto);

    RestResponseBody deleteRule(String uuid);
}