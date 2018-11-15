package com.mrray.datadesensitiveserver.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.mrray.datadesensitiveserver.entity.domain.Algorithm;
import com.mrray.datadesensitiveserver.entity.domain.ScanRecord;
import com.mrray.datadesensitiveserver.entity.dto.SetColumnsDto;
import com.mrray.datadesensitiveserver.entity.vo.RestResponseBody;
import com.mrray.datadesensitiveserver.repository.AlgorithmRepository;
import com.mrray.datadesensitiveserver.repository.DesensitiveRecordRepository;
import com.mrray.datadesensitiveserver.repository.ScanRecordRepository;
import com.mrray.datadesensitiveserver.service.ScanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class ScanServiceImpl implements ScanService {
    @Value("${constant.samples}")
    private int samples;
    private static final String PACKAGE = "com.mrray.datadesensitiveserver.algorithm.";
    private final AlgorithmRepository algorithmRepository;
    private final ScanRecordRepository scanRecordRepository;
    private final DesensitiveRecordRepository desensitiveRecordRepository;
    //private final DatasourceManagementService datasourceManagementService;

    @Autowired
    public ScanServiceImpl(AlgorithmRepository algorithmRepository, ScanRecordRepository scanRecordRepository, DesensitiveRecordRepository desensitiveRecordRepository) {
        this.algorithmRepository = algorithmRepository;
        this.scanRecordRepository = scanRecordRepository;
        this.desensitiveRecordRepository = desensitiveRecordRepository;
        //this.datasourceManagementService = datasourceManagementService;
    }

    /*@Override
    public RestResponseBody scan(ScanDto scanDto) {
        //List<Map<String, Object>> result = new ArrayList<>();
        RestResponseBody restResponseBody = new RestResponseBody<>();
        DatabaseInfo databaseInfo = new DatabaseInfo();
        BeanUtils.copyProperties(scanDto, databaseInfo);
        Connection connection = DatabaseUtils.connect(databaseInfo);
        if (connection == null) {
            return restResponseBody.setError("Failed connecting to the Database!");
        }
        for (String tableName : scanDto.getTableNames()) {
            if ("".equals(tableName)) {
                continue;
            }
            //Map<String, Object> tableInfo = new HashMap<>();
            //tableInfo.put("tableName", tableName);
            //result.add(tableInfo);
            databaseInfo.setTableName(tableName);
            List<Map<String, Object>> columns = null;
            try {
                columns = DatabaseUtils.getColumns(connection, databaseInfo);
                DatabaseUtils.getValues(connection, columns, tableName, 0, samples);
            } catch (SQLException e) {
                continue;
            }
            List<Algorithm> algorithms = algorithmRepository.findAllByOriginalTrueOrderByPriorityAsc();
            Map<String, String> map = new HashMap<>();
            //List<Map<String, Object>> list = new ArrayList<>();
            //tableInfo.put("result", map);
            //tableInfo.put("result", list);
            for (Map<String, Object> column : columns) {
                Object columnName = column.get("columnName");
                List<String> values = (List<String>) column.get("values");
                map.put((String) columnName, "");
                //Map<String, Object> columnMap = new HashMap<>();
                //columnMap.put("columnName", columnName);
                //columnMap.put("columnType", column.get("columnType"));
                //list.add(columnMap);
                for (Algorithm algorithm : algorithms) {
                    String className = PACKAGE + algorithm.getClassName();
                    boolean match;
                    try {
                        Class<?> aClass = Class.forName(className);
                        BaseAlgorithm baseAlgorithm = (BaseAlgorithm) aClass.newInstance();
                        match = baseAlgorithm.match(values);
                    } catch (Exception e) {
                        return restResponseBody;
                    }
                    if (match) {
                        if((className.contains("TelephoneAlgorithm")||className.contains("CellphoneAlgorithm")||className.contains("IDAlgorithm")||className.contains("SocialCreditAlgorithm"))&&!((String)column.get("columnType")).equalsIgnoreCase("VARCHAR")){
                            continue;
                        }
                        map.put((String) columnName, algorithm.getUuid());
                        //columnMap.put("type", algorithm.getName());
                        //List<ModeVo> modes = new ArrayList<>();
                        //for (Mode mode : algorithm.getModes()) {
                        //    ModeVo modeVo = new ModeVo();
                        //BeanUtils.copyProperties(mode, modeVo);
                        //modes.add(modeVo);
                        //}
                        //columnMap.put("modes", modes);
                        break;
                    }
                }

            }
            ScanRecord scanRecord = scanRecordRepository.findByTableName(tableName);
            if (scanRecord == null || !databaseInfo.getDatabaseName().equalsIgnoreCase(scanRecord.getDatabaseName())) {
                scanRecord = new ScanRecord();
                BeanUtils.copyProperties(databaseInfo, scanRecord);
            }
            scanRecord.setResult(JSONObject.toJSONString(map));
            scanRecord.setRows(DatabaseUtils.getRows(connection, tableName));
            SysUtils.save(scanRecordRepository, scanRecord);
        }
        DatabaseUtils.deleteTable(connection, databaseInfo.getTableName());
        DatabaseUtils.closeConnection(connection);
        return restResponseBody;
    }*/

    /*@Override
    public RestResponseBody scanSingle(ScanSingleDto scanSingleDto) {
        Map<String, Object> result = new HashMap<>();
        RestResponseBody restResponseBody = new RestResponseBody<>().setData(result);
        //DatabaseInfo databaseInfo = new DatabaseInfo();
        //BeanUtils.copyProperties(scanSingleDto, databaseInfo);
        //发起抽取
        ExtractDto extractDto = new ExtractDto();
        extractDto.setId(scanSingleDto.getExtractId());
        extractDto.setTableName(scanSingleDto.getTableName());
        RestResponseBody<DatabaseInfo> databaseInfoRestResponseBody = datasourceManagementService.synchrExtract(extractDto);
        if (!"success".equalsIgnoreCase(databaseInfoRestResponseBody.getMessage())) {
            return restResponseBody.setError("extract failed");
        }
        DatabaseInfo databaseInfo = databaseInfoRestResponseBody.getData();
        Connection connection = DatabaseUtils.connect(databaseInfo);
        String tableName = databaseInfo.getTableName();
        if (connection == null) {
            return restResponseBody.setError("Failed connecting to the Database!");
        }
        //result.put("tableName", tableName);
        //databaseInfo.setTableName(tableName);
        List<Map<String, Object>> columns;
        try {
            columns = DatabaseUtils.getColumns(connection, databaseInfo);
            DatabaseUtils.getValues(connection, columns, tableName, 0, samples);
        } catch (SQLException e) {
            return restResponseBody.setError("读取数据库失败");
        }
        List<Algorithm> algorithms = algorithmRepository.findAllByOriginalTrueOrderByPriorityAsc();
        Map<String, String> map = new HashMap<>();
        List<Map<String, Object>> list = new ArrayList<>();
        //tableInfo.put("result", map);
        result.put("columns", list);

        List<Algorithm> all = algorithmRepository.findAll();
        List<Map<String, Object>> allList = new ArrayList<>();
        Map<String, Object> falseMap = new HashMap<>();
        falseMap.put("name", "");
        falseMap.put("className", "");
        falseMap.put("modes", new ArrayList<>());
        allList.add(falseMap);
        for (Algorithm algorithm1 : all) {
            Map<String, Object> algorithmMap = new HashMap<>();
            algorithmMap.put("name", algorithm1.getName());
            algorithmMap.put("className", algorithm1.getClassName());
            List<ModeVo> modes = new ArrayList<>();
            for (Mode mode : algorithm1.getModes()) {
                ModeVo modeVo = new ModeVo();
                BeanUtils.copyProperties(mode, modeVo);
                modes.add(modeVo);
            }
            algorithmMap.put("modes", modes);
            allList.add(algorithmMap);
        }

        for (Map<String, Object> column : columns) {
            String columnName = (String) column.get("columnName");
            List<String> values = (List<String>) column.get("values");
            map.put(columnName, "");
            Map<String, Object> columnMap = new HashMap<>();
            columnMap.put("columnName", columnName);
            columnMap.put("columnType", column.get("columnType"));
            columnMap.put("type", allList);
            list.add(columnMap);

            for (Algorithm algorithm : algorithms) {
                String className = PACKAGE + algorithm.getClassName();
                boolean match;
                try {
                    Class<?> aClass = Class.forName(className);
                    BaseAlgorithm baseAlgorithm = (BaseAlgorithm) aClass.newInstance();
                    match = baseAlgorithm.match(values);
                } catch (Exception e) {
                    return restResponseBody;
                }
                if (match) {
                    if((className.contains("TelephoneAlgorithm")||className.contains("CellphoneAlgorithm")||className.contains("IDAlgorithm")||className.contains("SocialCreditAlgorithm"))&&!((String)column.get("columnType")).equalsIgnoreCase("VARCHAR")){
                        continue;
                    }
                    List<Map<String, Object>> newList = new LinkedList<>();
                    map.put(columnName, algorithm.getUuid());
                    for (Map<String, Object> temp : allList) {
                        if (temp.get("className").equals(algorithm.getClassName())) {
                            newList.add(0, temp);
                        } else {
                            newList.add(temp);
                        }
                    }
                    columnMap.put("type", newList);
                    break;
                }
            }
        }
        ScanRecord scanRecord = scanRecordRepository.findByTableName(tableName);
        if (scanRecord == null || !databaseInfo.getDatabaseName().equalsIgnoreCase(scanRecord.getDatabaseName())) {
            scanRecord = new ScanRecord();
            BeanUtils.copyProperties(databaseInfo, scanRecord);
        }
        result.put("scanRecord", scanRecord.getUuid());
        scanRecord.setResult(JSONObject.toJSONString(map));
        scanRecord.setRows(DatabaseUtils.getRows(connection, tableName));
        SysUtils.save(scanRecordRepository, scanRecord);
        DatabaseUtils.deleteTable(connection, databaseInfo.getTableName());
        DatabaseUtils.closeConnection(connection);
        return restResponseBody;
    }*/

    @Override
    public RestResponseBody column(SetColumnsDto setColumnsDto) {
        ScanRecord scanRecord = scanRecordRepository.findByUuid(setColumnsDto.getScanRecord());
        scanRecord.setColumns(JSONObject.toJSONString(setColumnsDto.getColumnDtos()));
        scanRecordRepository.saveAndFlush(scanRecord);
        return new RestResponseBody();
    }

    @Override
    public RestResponseBody record(ArrayList<String> tables) {
        RestResponseBody<Map<String, Object>> restResponseBody = new RestResponseBody<>();
        Map<String, Long> top = new HashMap<>();
        List<Algorithm> algorithms = algorithmRepository.findAll();
        for (Algorithm algorithm : algorithms) {
            top.put(algorithm.getName(), 0L);
        }
        Map<String, Object> information = new HashMap<>();
        double total = 0d;
        double a = 0d;
        Set<String> types = new HashSet<>();
        Set<String> sensitiveColumns = new HashSet<>();
        for (String table : tables) {
            ScanRecord scanRecord = scanRecordRepository.findByTableName(table);
            if (scanRecord == null) {
                continue;
            }
            Map<String, String> result = (Map<String, String>) JSONObject.parse(scanRecord.getResult());
            Set<String> columns = result.keySet();
            for (String column : columns) {
                total++;
                String uuid = result.get(column);
                if (!"".equals(uuid)) {
                    a++;
                    Algorithm algorithm = algorithmRepository.findByUuid(uuid);
                    types.add(algorithm.getName());
                    sensitiveColumns.add(column);
                    top.put(algorithm.getName(), top.get(algorithm.getName()) + scanRecord.getRows());
                }
            }
        }
        List<List<Object>> topResult = sort(top);
        BigDecimal bigDecimal;
        if (total > 1) {
            bigDecimal = BigDecimal.valueOf(a / total * 100);
        } else {
            bigDecimal = BigDecimal.valueOf(0);
        }
        information.put("types", types);
        information.put("percent", bigDecimal.setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue());
        information.put("sensitiveColumns", sensitiveColumns);
        information.put("top", topResult);
        return restResponseBody.setData(information);
    }

    @Override
    public RestResponseBody index() {
        RestResponseBody<Map<String, Object>> restResponseBody = new RestResponseBody<>();
        Map<String, Object> result = new HashMap<>();
        restResponseBody.setData(result);
        Long counts = desensitiveRecordRepository.sumCounts();
        result.put("process", counts);
        Map<String, Long> top = new HashMap<>();
        List<Algorithm> algorithms = algorithmRepository.findAll();
        for (Algorithm algorithm : algorithms) {
            top.put(algorithm.getName(), 0L);
        }
        List<ScanRecord> scanRecords = scanRecordRepository.findAll();
        for (ScanRecord scanRecord : scanRecords) {
            Long recordRows = scanRecord.getRows();
            Map<String, String> record = (Map<String, String>) JSONObject.parse(scanRecord.getResult());
            Collection<String> values = record.values();
            for (String value : values) {
                if ("".equals(value)) {
                    continue;
                }
                Algorithm algorithm = algorithmRepository.findByUuid(value);
                top.put(algorithm.getName(), top.get(algorithm.getName()) + recordRows);
            }
        }
        List<List<Object>> topResult = sort(top);
        result.put("top", topResult);
        return restResponseBody;
    }

    private List<List<Object>> sort(Map<String, Long> top) {
        List<Map.Entry<String, Long>> list = new ArrayList<>();
        list.addAll(top.entrySet());
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = 0; j < list.size() - i - 1; j++) {
                if (list.get(j).getValue() < list.get(j + 1).getValue()) {
                    Map.Entry<String, Long> temp = list.get(j);
                    list.set(j, list.get(j + 1));
                    list.set(j + 1, temp);
                }
            }
        }
        List<List<Object>> topResult = new ArrayList<>();
        for (Map.Entry<String, Long> entry : list) {
            List<Object> temp = new ArrayList<>();
            temp.add(entry.getKey());
            temp.add(entry.getValue());
            topResult.add(temp);
        }
        return topResult;
    }
}