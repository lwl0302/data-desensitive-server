package com.mrray.datadesensitiveserver.service;

import com.mrray.datadesensitiveserver.entity.dto.TaskDto;
import com.mrray.datadesensitiveserver.entity.vo.RestResponseBody;

public interface DesensitiveService {
    //RestResponseBody desensitive(DatabaseInfo databaseInfo, String ruleId, String scan);

    //RestResponseBody status(String taskUuid);

    RestResponseBody delete(String taskUuid);

    RestResponseBody getDatabaseInfo(String taskUuid);

    RestResponseBody desensitive(TaskDto taskDto);
}