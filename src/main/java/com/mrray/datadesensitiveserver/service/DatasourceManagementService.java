package com.mrray.datadesensitiveserver.service;

import com.mrray.datadesensitiveserver.entity.dto.DatabaseInfo;
import com.mrray.datadesensitiveserver.entity.dto.ExtractDto;
import com.mrray.datadesensitiveserver.entity.vo.RestResponseBody;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("datasource-management-server")
public interface DatasourceManagementService {
    @PostMapping("/synchr_extract_task")
    RestResponseBody<DatabaseInfo> synchrExtract(@RequestBody ExtractDto syschrExtractDto);
}
