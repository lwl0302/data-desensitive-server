package com.mrray.datadesensitiveserver.controller;

import com.mrray.datadesensitiveserver.entity.dto.RuleDto;
import com.mrray.datadesensitiveserver.service.RuleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

//@RestController
//@RequestMapping("/rules")
public class RuleController {
    private final RuleService ruleService;

    //@Autowired
    public RuleController(RuleService ruleService) {
        this.ruleService = ruleService;
    }

    //@ApiOperation(value = "获取规则列表")
    //@GetMapping("")
    //public ResponseEntity getRuleList(RuleQueryDto ruleQueryDto) {
    //    return ResponseEntity.ok(ruleService.getRuleList(ruleQueryDto));
    //}

    //@ApiOperation(value = "添加规则")
    //@ApiImplicitParam(name = "ruleDto", value = "规则", required = true, dataType = "RuleDto")
    //@PostMapping("")
    //public ResponseEntity postRule(@RequestBody RuleDto ruleDto) {
        //return ResponseEntity.ok(ruleService.postRule(ruleDto));
    //}

    //@ApiOperation(value = "修改规则")
    //@ApiImplicitParams({
    //        @ApiImplicitParam(name = "uuid", value = "规则id", required = true, dataType = "String", paramType = "path"),
    //        @ApiImplicitParam(name = "ruleDto", value = "规则", required = true, dataType = "RuleDto")
    //})
    //@PutMapping(value = "/{uuid}")
    public ResponseEntity putRule(@PathVariable String uuid, @RequestBody RuleDto ruleDto) {
        return ResponseEntity.ok(ruleService.putRule(uuid, ruleDto));
    }

    //@ApiOperation(value = "删除规则")
    //@ApiImplicitParam(name = "uuid", value = "规则id", required = true, dataType = "String", paramType = "path")
    //@DeleteMapping("/{uuid}")
    public ResponseEntity deleteRule(@PathVariable String uuid) {
        return ResponseEntity.ok(ruleService.deleteRule(uuid));
    }
}