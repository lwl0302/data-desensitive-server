package com.mrray.datadesensitiveserver.service;

import com.mrray.datadesensitiveserver.entity.vo.RestResponseBody;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("task-management-server")
public interface TaskManagementService {
    @PostMapping("/api/v1/tasks/notice/desensitive")
    RestResponseBody desensitive(@RequestBody RestResponseBody restResponseBody);
}
