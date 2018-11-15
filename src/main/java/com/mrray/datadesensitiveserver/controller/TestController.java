package com.mrray.datadesensitiveserver.controller;

import com.alibaba.fastjson.JSON;
import com.mrray.datadesensitiveserver.async.AsyncTasks;
import com.mrray.datadesensitiveserver.entity.dto.SaveDataDto;
import com.mrray.datadesensitiveserver.entity.dto.TaskDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@RestController
//@RequestMapping("")
public class TestController {
    @Resource
    private AsyncTasks asyncTasks;

    @PostMapping("/api/v2/daas/meta/dataQuality/getDataByDataSetId")
    public ResponseEntity test(@RequestBody TaskDto taskDto) {
        System.out.println(taskDto);
        Map<String, Object> map = new HashMap<>();
        map.put("code", 200);
        map.put("message", "操作成功");
        Map<String, Object> result = new HashMap<>();
        map.put("result", result);
        Map<String, Object> data = new HashMap<>();
        result.put("data", data);
        data.put("dataSetId", "486");
        data.put("totalNum", 3);
        List<Map<String, Object>> datas = new ArrayList<>();
        data.put("datas", datas);
        data.put("sinkTableName", "sinkTableName");
        Map<String, Object> row1 = new HashMap<>();
        row1.put("column1", "510113199103026210");
        row1.put("column2", "6217996900043690867");
        row1.put("column3", "18708139628");
        row1.put("column4", "2018-08-01 16:53:46");
        datas.add(row1);
        Map<String, Object> row2 = new HashMap<>();
        row2.put("column1", "510113199103026210");
        row2.put("column2", "6217996900043690867");
        row2.put("column3", "18708139628");
        row2.put("column4", "2018-08-01 16:53:46");
        datas.add(row2);
        Map<String, Object> row3 = new HashMap<>();
        row3.put("column1", "510113199103026210");
        row3.put("column2", "6217996900043690867");
        row3.put("column3", "18708139628");
        row3.put("column4", "2018-08-01 16:53:46");
        datas.add(row3);
        return ResponseEntity.ok(JSON.toJSONString(map));
    }

    @PostMapping("/api/v2/daas/meta/dataQuality/saveData")
    public ResponseEntity test(@RequestBody SaveDataDto saveDataDto) {
        System.out.println(saveDataDto.getSinkTableName());
        System.out.println(saveDataDto.getDataSetId());
        System.out.println(saveDataDto.getMediumType());
        System.out.println(saveDataDto.getDesensitizationData());
        return ResponseEntity.ok("success");
    }

    //@GetMapping("/test")
    //public ResponseEntity test2() {
    //    return ResponseEntity.ok(asyncTasks.getData("123", "23", 2, 1000, 10));
    //}
}
