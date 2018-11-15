package com.mrray.datadesensitiveserver.controller;

import com.mrray.datadesensitiveserver.entity.dto.TaskDto;
import com.mrray.datadesensitiveserver.service.DesensitiveService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/desensitive")
public class DesensitiveConroller {
    private final DesensitiveService desensitiveService;

    @Autowired
    public DesensitiveConroller(DesensitiveService desensitiveService) {
        this.desensitiveService = desensitiveService;
    }

    @ApiOperation(value = "发起脱敏任务")
    //@ApiImplicitParams({
    //        @ApiImplicitParam(name = "targetInfo", value = "数据源连接信息", required = true, dataType = "DatabaseInfo"),
    //        @ApiImplicitParam(name = "ruleId", value = "规则id", required = false, dataType = "String", paramType = "query")
    //})
    @PostMapping("")
    public ResponseEntity desensitive(@RequestBody TaskDto taskDto) {
        return ResponseEntity.ok(desensitiveService.desensitive(taskDto));
    }

    /*@ApiOperation(value = "获取脱敏任务状态")
    @ApiImplicitParam(name = "taskUuid", value = "任务id", required = true, dataType = "String", paramType = "path")
    @GetMapping("{taskUuid}")
    public ResponseEntity status(@PathVariable String taskUuid) {
        return ResponseEntity.ok(desensitiveService.status(taskUuid));
    }*/

    //@ApiOperation(value = "删除脱敏结果表")
    //@ApiImplicitParam(name = "taskUuid", value = "任务id", required = true, dataType = "String", paramType = "path")
    //@DeleteMapping("{taskUuid}")
    //public ResponseEntity deleteTarget(@PathVariable String taskUuid) {
    //    return ResponseEntity.ok(desensitiveService.delete(taskUuid));
    //}

    //@GetMapping("{taskUuid}")
    //public ResponseEntity getDatabaseInfo(@PathVariable String taskUuid) {
    //    return ResponseEntity.ok(desensitiveService.getDatabaseInfo(taskUuid));
    //}
}