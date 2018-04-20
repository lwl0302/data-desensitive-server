package com.mrray.datadesensitiveserver.controller;

import com.mrray.datadesensitiveserver.entity.dto.PageQueryDto;
import com.mrray.datadesensitiveserver.service.AlgorithmService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/algorithms")
public class AlgorithmController {
    private final AlgorithmService algorithmService;

    @Autowired
    public AlgorithmController(AlgorithmService algorithmService) {
        this.algorithmService = algorithmService;
    }

    @ApiOperation(value = "获取算法列表")
    @GetMapping("")
    public ResponseEntity getAlgorithmList(PageQueryDto pageQueryDto, boolean all) {
        return ResponseEntity.ok(algorithmService.getAlgorithmList(pageQueryDto,all));
    }

    /*@ApiOperation(value = "删除算法")
    @ApiImplicitParam(name = "algorithmId", value = "算法id", required = true, dataType = "String")
    @DeleteMapping("/{algorithmId}")
    public String deleteAlgorithm(@PathVariable String algorithmId) {
        return "success";
    }*/

    @ApiOperation(value = "导入新算法", consumes = "multipart/form-data")
    @ApiParam(name = "file", value = "jar包", type = "file")
    @PostMapping(value = "/load")
    @ResponseBody
    public ResponseEntity load(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(algorithmService.load(file));
    }

    @ApiOperation(value = "获取序列号")
    @GetMapping(value = "/serial")
    @ResponseBody
    public ResponseEntity serial() {
        return ResponseEntity.ok(algorithmService.serial());
    }
}