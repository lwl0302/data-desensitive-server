package com.mrray.datadesensitiveserver.service;

import com.mrray.datadesensitiveserver.entity.vo.RestResponseBody;
import org.springframework.web.multipart.MultipartFile;

public interface AlgorithmService {
    RestResponseBody getAlgorithmList();

    RestResponseBody load(MultipartFile file);

    RestResponseBody serial();
}