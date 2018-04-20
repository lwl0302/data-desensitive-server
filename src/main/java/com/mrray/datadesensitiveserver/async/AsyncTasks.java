package com.mrray.datadesensitiveserver.async;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mrray.datadesensitiveserver.algorithm.BaseAlgorithm;
import com.mrray.datadesensitiveserver.entity.domain.*;
import com.mrray.datadesensitiveserver.entity.dto.DatabaseInfo;
import com.mrray.datadesensitiveserver.entity.vo.RestResponseBody;
import com.mrray.datadesensitiveserver.repository.*;
import com.mrray.datadesensitiveserver.service.TaskManagementService;
import com.mrray.datadesensitiveserver.utils.DatabaseUtils;
import com.mrray.datadesensitiveserver.utils.SysUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Future;

@Component
public class AsyncTasks {
    @Value("${constant.rows}")
    private int rows;//单次处理行数
    //private static final String PACKAGE = "com.mrray.datadesensitiveserver.algorithm.";
    private final RuleRepository ruleRepository;
    private final AlgorithmRepository algorithmRepository;
    private final ScanRecordRepository scanRecordRepository;
    private final ModeRepository modeRepository;
    private final TaskManagementService taskManagementService;
    private final DesensitiveRecordRepository desensitiveRecordRepository;
    //private static final List<String> SUPPORT = Arrays.asList("INT", "VARCHAR", "DATETIME", "DATE", "TIME", "TIMESTAMP", "YEAR", "TINYTEXT", "TEXT", "MEDIUMTEXT", "LONGTEXT", "BIGINT");
    private static Logger logger = LoggerFactory.getLogger("AsyncTasks");

    @Autowired
    public AsyncTasks(RuleRepository ruleRepository, AlgorithmRepository algorithmRepository, ScanRecordRepository scanRecordRepository, ModeRepository modeRepository, TaskManagementService taskManagementService, DesensitiveRecordRepository desensitiveRecordRepository) {
        this.ruleRepository = ruleRepository;
        this.algorithmRepository = algorithmRepository;
        this.scanRecordRepository = scanRecordRepository;
        this.modeRepository = modeRepository;
        this.taskManagementService = taskManagementService;
        this.desensitiveRecordRepository = desensitiveRecordRepository;
    }

    @Async
    public Future<RestResponseBody> process(DatabaseInfo databaseInfo, String ruleId, String newTableName, String recordUuid, String scan) {
        RestResponseBody<Map<String, Object>> restResponseBody = new RestResponseBody<>();
        AsyncResult<RestResponseBody> asyncResult = new AsyncResult<>(restResponseBody);
        String tableName = databaseInfo.getTableName();
        Map<String, Object> progress = new HashMap<>();
        progress.put("recordUuid", recordUuid);
        progress.put("mainTaskId", databaseInfo.getMainTaskId());
        databaseInfo.setTableName(newTableName);
        progress.put("databaseInfo", databaseInfo);
        restResponseBody.setData(progress);

        //连接到数据库
        Connection connection = DatabaseUtils.connect(databaseInfo);
        if (connection == null) {
            restResponseBody.setError("无法连接到中间库");
            taskManagementService.desensitive(restResponseBody);
            return asyncResult;
        }

        //创建脱敏结果表
        boolean createTable = DatabaseUtils.createTable(connection, tableName, newTableName);
        if (!createTable) {
            DatabaseUtils.closeConnection(connection);
            restResponseBody.setError("建立脱敏结果表失败");
            taskManagementService.desensitive(restResponseBody);
            return asyncResult;
        }

        //获取目标表行数
        Long total = DatabaseUtils.getRows(connection, tableName);
        if (total == 0) {
            DatabaseUtils.closeConnection(connection);
            taskManagementService.desensitive(restResponseBody);
            return asyncResult;
        }
        logger.info("行数 " + total);

        String algorithmKey = "algorithm";
        List<Map<String, Object>> columns;
        Long sensitive = 0L;
        //获取column信息
        try {
            columns = DatabaseUtils.getColumns(connection, databaseInfo);
        } catch (SQLException e) {
            DatabaseUtils.closeConnection(connection);
            restResponseBody.setError("获取字段信息异常");
            taskManagementService.desensitive(restResponseBody);
            return asyncResult;
        }

        if (StringUtils.isNotBlank(ruleId)) {
            //查询扫描记录
            ScanRecord scanRecord = scanRecordRepository.findByUuid(scan);
            if (scanRecord == null) {
                DatabaseUtils.closeConnection(connection);
                restResponseBody.setError("获取扫描结果失败");
                taskManagementService.desensitive(restResponseBody);
                return asyncResult;
            }
            Map<String, String> result = (Map<String, String>) JSONObject.parse(scanRecord.getResult());

            //查询脱敏规则
            Rule rule = ruleRepository.findByUuid(ruleId);
            if (rule == null) {
                DatabaseUtils.closeConnection(connection);
                restResponseBody.setError("获取脱敏规则失败");
                taskManagementService.desensitive(restResponseBody);
                return asyncResult;
            }
            Map<String, String> details = (Map<String, String>) JSONObject.parse(rule.getDetails());

            //添加算法、脱敏方式到column信息
            for (Map<String, Object> column : columns) {
                String columnName = (String) column.get("columnName");
                String uuid = result.get(columnName);
                column.put(algorithmKey, "");
                column.put("mode", "");
                column.put("args", new JSONArray());
                if ("".equals(uuid)) {
                    continue;
                }
                sensitive++;
                Algorithm algorithm = algorithmRepository.findByUuid(uuid);
                String auuid = algorithm.getUuid();
                column.put(algorithmKey, algorithm.getClassName());
                Mode mode = modeRepository.findByUuid(details.get(auuid));
                column.put("mode", mode.getMethodName());
                if (details.containsKey(auuid + "arg")) {
                    JSONArray array = new JSONArray();
                    array.add(details.get(auuid + "arg"));
                    column.put("args", array);
                }
            }
        } else if (StringUtils.isNotBlank(scan)) {
            ScanRecord scanRecord = scanRecordRepository.findByUuid(scan);
            if (scanRecord == null) {
                DatabaseUtils.closeConnection(connection);
                restResponseBody.setError("获取扫描信息异常");
                taskManagementService.desensitive(restResponseBody);
                return asyncResult;
            }
            logger.info("scan record " + scanRecord.getUuid());
            List<Map<String, Object>> tempColumns = (List<Map<String, Object>>) JSONObject.parse(scanRecord.getColumns());
            Set<String> columnNames = new HashSet<>();
            for (Map<String, Object> column : columns) {
                columnNames.add((String) column.get("columnName"));
            }
            columns.clear();
            for (Map<String, Object> column : tempColumns) {
                if (columnNames.contains(column.get("columnName"))) {
                    columns.add(column);
                    if (StringUtils.isNotEmpty((String) column.get("algorithm"))) {
                        sensitive++;
                    }
                }
            }
        } else {
            DatabaseUtils.closeConnection(connection);
            restResponseBody.setError("Failed to get scan record !");
            taskManagementService.desensitive(restResponseBody);
            return asyncResult;
        }
        //分段执行脱敏
        String valuesKey = "values";
        Double percent = 0d;
        Double part = (double) rows / (double) total * 100;
        Long time = total / rows;
        if (total % rows != 0) {
            time++;
        }
        int offset = 0;
        long count = total * sensitive;
        while (total > 0) {
            //查询数据放入columns
            try {
                DatabaseUtils.getValues(connection, columns, tableName, offset, rows);
            } catch (SQLException e) {
                DatabaseUtils.closeConnection(connection);
                restResponseBody.setError("查询数据异常");
                taskManagementService.desensitive(restResponseBody);
                return asyncResult;
            }
            logger.info("get value success");
            int size = 0;
            for (Map<String, Object> column : columns) {
                //非SUPPORT类型暂不做处理
                //if (!SUPPORT.contains(column.get("columnType"))) {
                //    continue;
                //}
                List<String> values = (List<String>) column.get(valuesKey);
                size = values.size();
                String mode = (String) column.get("mode");
                if ("".equals(mode)) {
                    continue;
                }
                String algorithm = (String) column.get(algorithmKey);
                JSONArray args = (JSONArray) column.get("args");
                if (args == null) {
                    restResponseBody.setError("Failed to get algorithm args !");
                    taskManagementService.desensitive(restResponseBody);
                    return asyncResult;
                }
                Object[] objects = new Object[args.size()];
                for (int i = 0; i < args.size(); i++) {
                    objects[i] = args.get(i);
                }
                logger.info("algorithm " + algorithm + " mode " + mode);
                //多态+反射执行单列脱敏
                try {
                    Class<?> aClass = SysUtils.getClazz(algorithm);
                    //Class<?> aClass = null;
                    if (aClass == null) {
                        logger.info("class null");
                        column.put(valuesKey, values);
                        continue;
                    }
                    logger.info("class " + aClass.getName());
                    BaseAlgorithm baseAlgorithm = (BaseAlgorithm) aClass.newInstance();
                    values = baseAlgorithm.desensitive(values, mode, objects);
                    column.put(valuesKey, values);
                } catch (Throwable e) {
                    DatabaseUtils.closeConnection(connection);
                    logger.error(e.getMessage());
                    restResponseBody.setError("脱敏算法调用异常");
                    taskManagementService.desensitive(restResponseBody);
                    return asyncResult;
                }
            }
            //将列数组转为行数组
            List<List<String>> valuesToInsert = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                List<String> row = new ArrayList<>();
                for (Map<String, Object> column : columns) {
                    List<String> values = (List<String>) column.get(valuesKey);
                    if (values.size() > i) {
                        row.add(values.get(i));
                    } else {
                        row.add(null);
                    }

                }
                valuesToInsert.add(row);
            }
            try {
                DatabaseUtils.insert(connection, newTableName, valuesToInsert, columns.size());
            } catch (SQLException e) {
                DatabaseUtils.closeConnection(connection);
                logger.error("数据插入异常");
                restResponseBody.setError("数据插入异常");
                taskManagementService.desensitive(restResponseBody);
                return asyncResult;
            }
            offset += rows;
            total -= rows;
            time--;
            percent += part;
            if (time == 0) {
                percent = 100d;
            }
            BigDecimal bigDecimal = BigDecimal.valueOf(percent);
            progress.put("percent", bigDecimal.setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
        }
        try {
            taskManagementService.desensitive(restResponseBody);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        //DatabaseUtils.deleteTable(connection, tableName);
        DatabaseUtils.closeConnection(connection);
        DesensitiveRecord desensitiveRecord = desensitiveRecordRepository.findByUuid(recordUuid);
        desensitiveRecord.setResult("success");
        desensitiveRecord.setCounts(count);
        desensitiveRecordRepository.saveAndFlush(desensitiveRecord);
        return asyncResult;
    }

    /*public static void main(String[] args) throws Exception {
        byte[] lwl = EncrypDES.Encrytor("lwl");
        byte[] lwl3 = Encryp3DES.Encrytor("lwl");
        byte[] lwla = EncrypAES.Encrytor("lwl");
        String s = SysUtils.encryptBASE64(lwl);
        String s1 = SysUtils.encryptBASE64(lwl3);
        String sa = SysUtils.encryptBASE64(lwla);
        System.out.println(s);
        System.out.println(s1);
        System.out.println(sa);
        System.out.println(new String(EncrypDES.Decryptor(SysUtils.decryptBASE64(s))));
        System.out.println(new String(Encryp3DES.Decryptor(SysUtils.decryptBASE64(s1))));
        System.out.println(new String(EncrypAES.Decryptor(SysUtils.decryptBASE64(sa))));
        //System.out.println(new String(EncrypDES.Decryptor(SysUtils.decryptBASE64("JKScmolwQSI="))));
    }*/
}