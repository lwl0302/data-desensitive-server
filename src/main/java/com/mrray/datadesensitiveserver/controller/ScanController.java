package com.mrray.datadesensitiveserver.controller;

import com.mrray.datadesensitiveserver.entity.dto.SetColumnsDto;
import com.mrray.datadesensitiveserver.service.ScanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;

//@RestController
//@RequestMapping("/scan")
public class ScanController {
    private final ScanService scanService;

    //@Autowired
    public ScanController(ScanService scanService) {
        this.scanService = scanService;
    }

    //@ApiOperation(value = "开始扫描")
    //@ApiImplicitParam(name = "scanDto", value = "连接信息", required = true, dataType = "ScanDto")
    //@PostMapping("")
    //public ResponseEntity scan(@RequestBody ScanDto scanDto) {
    //    return ResponseEntity.ok(scanService.scan(scanDto));
    //}

    /* @ApiOperation(value = "开始扫描")
     //@ApiImplicitParam(name = "ScanSingleDto", value = "连接信息", required = true, dataType = "ScanSingleDto")
     @PostMapping("/scanSingle")
     public ResponseEntity scanSingle(@RequestBody ScanSingleDto scanSingleDto) {
         return ResponseEntity.ok(scanService.scanSingle(scanSingleDto));
     }
 */
    //@ApiOperation(value = "上传脱敏配置")
    //@ApiImplicitParam(name = "ScanSingleDto", value = "连接信息", required = true, dataType = "ScanSingleDto")
    //@PostMapping("/column")
    public ResponseEntity column(@RequestBody SetColumnsDto setColumnsDto) {
        return ResponseEntity.ok(scanService.column(setColumnsDto));
    }

    //@ApiOperation(value = "扫描记录")
    //@ApiImplicitParam(name = "tables", value = "表名", required = true, dataType = "ArrayList<String>", paramType = "body")
    //@PostMapping("/record")
    public ResponseEntity record(@RequestBody ArrayList<String> tables) {
        return ResponseEntity.ok(scanService.record(tables));
    }

    //@ApiOperation(value = "首页信息")
    //@GetMapping("/index")
    public ResponseEntity index() {
        return ResponseEntity.ok(scanService.index());
    }
}