package com.mrray.datadesensitiveserver.service.impl;

import com.mrray.datadesensitiveserver.async.AsyncTasks;
import com.mrray.datadesensitiveserver.entity.domain.DesensitiveRecord;
import com.mrray.datadesensitiveserver.entity.dto.DatabaseInfo;
import com.mrray.datadesensitiveserver.entity.vo.RestResponseBody;
import com.mrray.datadesensitiveserver.repository.DesensitiveRecordRepository;
import com.mrray.datadesensitiveserver.service.DesensitiveService;
import com.mrray.datadesensitiveserver.utils.DatabaseUtils;
import com.mrray.datadesensitiveserver.utils.SysUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

@Service
public class DesensitiveServiceImpl implements DesensitiveService {
    //private static final Map<String, Future<RestResponseBody>> tasks = new HashMap<>();
    private final AsyncTasks asyncTasks;
    private final DesensitiveRecordRepository desensitiveRecordRepository;
    private static String iderror = "Task uuid is not found !";

    @Autowired
    public DesensitiveServiceImpl(AsyncTasks asyncTasks, DesensitiveRecordRepository desensitiveRecordRepository) {
        this.asyncTasks = asyncTasks;
        this.desensitiveRecordRepository = desensitiveRecordRepository;
    }

    @Override
    public RestResponseBody desensitive(DatabaseInfo databaseInfo, String ruleId, String scan) {
        String newTableName = String.format("%s_%s", databaseInfo.getTableName(), RandomStringUtils.random(4, true, true).toLowerCase());
        DesensitiveRecord desensitiveRecord = new DesensitiveRecord();
        //Future<RestResponseBody> process =
        asyncTasks.process(databaseInfo, ruleId, newTableName, desensitiveRecord.getUuid(), scan);
        BeanUtils.copyProperties(databaseInfo, desensitiveRecord);
        desensitiveRecord.setResult("running");
        desensitiveRecord.setNewTableName(newTableName);
        SysUtils.save(desensitiveRecordRepository, desensitiveRecord);
        String taskUuid = desensitiveRecord.getUuid();
        //tasks.put(taskUuid, process);
        Map<String, String> map = new HashMap<>();
        map.put("taskUuid", taskUuid);
        return new RestResponseBody<>().setData(map);
    }

    /*@Override
    public RestResponseBody status(String taskUuid) {
        String status = "status";
        RestResponseBody<Map<String, String>> restResponseBody = new RestResponseBody<>();
        Map<String, String> result = new HashMap<>();
        Future<RestResponseBody> future = tasks.get(taskUuid);
        DesensitiveRecord desensitiveRecord = desensitiveRecordRepository.findByUuid(taskUuid);
        if (desensitiveRecord == null) return restResponseBody.setError(iderror);

        if ("success".equals(desensitiveRecord.getResult())) {
            result.put(status, "done");
            result.put("newTableName", desensitiveRecord.getNewTableName());
            return restResponseBody.setData(result);
        }
        try {
            if (future.isDone()) {
                restResponseBody = future.get();
                if (restResponseBody.getMessage().equals("SUCCESS")) {
                    result.put(status, "done");
                    result.put("newTableName", desensitiveRecord.getNewTableName());
                    restResponseBody.setData(result);
                    desensitiveRecord.setResult("success");
                    SysUtils.save(desensitiveRecordRepository, desensitiveRecord);
                } else {
                    desensitiveRecord.setResult("fail");
                    SysUtils.save(desensitiveRecordRepository, desensitiveRecord);
                }
                tasks.remove(taskUuid);
            } else {
                result.put(status, "running");
                restResponseBody.setData(result);
            }
        } catch (Exception e) {
            return restResponseBody.setError(iderror);
        }
        return restResponseBody;
    }*/

    @Override
    public RestResponseBody delete(String taskUuid) {
        RestResponseBody restResponseBody = new RestResponseBody();
        //if (tasks.get(taskUuid) != null) return restResponseBody.setError("Task is running !");

        DesensitiveRecord desensitiveRecord = desensitiveRecordRepository.findByUuid(taskUuid);
        if (desensitiveRecord == null) {
            return restResponseBody.setError(iderror);
        }

        if (!desensitiveRecord.getExist()) {
            return restResponseBody.setError("Table not exists !");
        }

        DatabaseInfo databaseInfo = new DatabaseInfo();
        BeanUtils.copyProperties(desensitiveRecord, databaseInfo);
        Connection connection = DatabaseUtils.connect(databaseInfo);
        DatabaseUtils.deleteTable(connection, desensitiveRecord.getNewTableName());
        desensitiveRecord.setExist(false);
        SysUtils.save(desensitiveRecordRepository, desensitiveRecord);
        return restResponseBody;
    }

    @Override
    public RestResponseBody getDatabaseInfo(String taskUuid) {
        RestResponseBody<DatabaseInfo> restResponseBody = new RestResponseBody<>();
        DesensitiveRecord desensitiveRecord = desensitiveRecordRepository.findByUuid(taskUuid);
        if (desensitiveRecord == null) {
            return restResponseBody.setError(iderror);
        }
        DatabaseInfo databaseInfo = new DatabaseInfo();
        BeanUtils.copyProperties(desensitiveRecord, databaseInfo);
        databaseInfo.setTableName(desensitiveRecord.getNewTableName());
        return restResponseBody.setData(databaseInfo);
    }
}