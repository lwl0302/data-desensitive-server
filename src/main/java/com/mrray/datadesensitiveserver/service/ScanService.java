package com.mrray.datadesensitiveserver.service;

import com.mrray.datadesensitiveserver.entity.dto.SetColumnsDto;
import com.mrray.datadesensitiveserver.entity.vo.RestResponseBody;

import java.util.ArrayList;

public interface ScanService {
    //RestResponseBody scan(ScanDto scanDto);

    //RestResponseBody scanSingle(ScanSingleDto scanSingleDto);

    RestResponseBody column(SetColumnsDto setColumnsDto);

    RestResponseBody record(ArrayList<String> tables);

    RestResponseBody index();
}