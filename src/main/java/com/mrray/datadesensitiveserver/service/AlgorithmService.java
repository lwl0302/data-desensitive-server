package com.mrray.datadesensitiveserver.service;

import com.mrray.datadesensitiveserver.entity.dto.PageQueryDto;
import com.mrray.datadesensitiveserver.entity.vo.RestResponseBody;
import org.springframework.web.multipart.MultipartFile;

public interface AlgorithmService {
    RestResponseBody getAlgorithmList(PageQueryDto pageQueryDto, boolean all);

    RestResponseBody load(MultipartFile file);

    RestResponseBody serial();
}