package com.mrray.datadesensitiveserver.async;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mrray.datadesensitiveserver.algorithm.BaseAlgorithm;
import com.mrray.datadesensitiveserver.entity.domain.Algorithm;
import com.mrray.datadesensitiveserver.entity.domain.Mode;
import com.mrray.datadesensitiveserver.entity.dto.TaskDto;
import com.mrray.datadesensitiveserver.entity.vo.TaskVo;
import com.mrray.datadesensitiveserver.repository.AlgorithmRepository;
import com.mrray.datadesensitiveserver.repository.ModeRepository;
import com.mrray.datadesensitiveserver.utils.SysUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
public class AsyncTasks {
    @Value("${constant.rows}")
    private int rows;//单次处理行数
    private final AlgorithmRepository algorithmRepository;
    private final ModeRepository modeRepository;
    //private static final List<String> SUPPORT = Arrays.asList("INT", "VARCHAR", "DATETIME", "DATE", "TIME", "TIMESTAMP", "YEAR", "TINYTEXT", "TEXT", "MEDIUMTEXT", "LONGTEXT", "BIGINT");
    private static Logger logger = LoggerFactory.getLogger("AsyncTasks");
    @Value("${daas-uri}")
    private String daasUri;
    @Value("${constant.samples}")
    private int samples;
    private static RestTemplate restTemplate = new RestTemplate();
    private static final String PACKAGE = "com.mrray.datadesensitiveserver.algorithm.";

    @Autowired
    public AsyncTasks(AlgorithmRepository algorithmRepository, ModeRepository modeRepository) {
        this.algorithmRepository = algorithmRepository;
        this.modeRepository = modeRepository;
        //this.taskManagementService = taskManagementService;
    }

    @Async
    public void process(TaskDto taskDto, Map<String, String> modesMap) {
        TaskVo taskVo = new TaskVo();
        BeanUtils.copyProperties(taskDto, taskVo);
        taskVo.setMediumType(Integer.valueOf(taskDto.getMediumType()));
        List<Map<String, Object>> columns = new ArrayList<>();
        //获取样本数据
        String mediumType = taskDto.getMediumType();
        String dataSetId = taskDto.getDataSetId();
        String taskId = taskDto.getTaskId();
        String accessToken = taskDto.getAccessToken();
        Map<String, Object> sampleData = getData(dataSetId, mediumType, accessToken, columns, 1, samples);
        if (sampleData == null) {
            logger.error("failed to get samples");
            recall(taskVo.setStatus(500), accessToken);
            return;
        }
        //获取目标表行数
        int total = (int) sampleData.get("total");
        //String sinkTableName = (String) sampleData.get("sinkTableName");
        String sinkTableName = getSinkTableName(dataSetId, mediumType, taskId, accessToken);
        taskVo.setSinkCatalog(sinkTableName);
        logger.info("行数 " + total);
        //敏感字段识别
        match(columns);

        //添加算法、脱敏方式到column信息
        for (Map<String, Object> column : columns) {
            if (!column.containsKey("algorithm")) {
                continue;
            }
            String algorithmId = (String) column.get("algorithm");
            Algorithm algorithm = algorithmRepository.findByUuid(algorithmId);
            column.put("algorithm", algorithm.getClassName());
            Mode mode = modeRepository.findByUuid(modesMap.get(algorithmId));
            column.put("mode", mode.getMethodName());
            //column.put("args", new JSONArray());
            //if (modesMap.containsKey(algorithmId + "arg")) {
            //    ((JSONArray) column.get("args")).add(modesMap.get(algorithmId + "arg"));
            //}
        }

        //分段执行脱敏
        int page = 1;
        while (total > 0) {
            int size;
            if (total < rows) {
                size = total;
            } else {
                size = rows;
            }
            //查询数据放入columns
            Map<String, Object> data = getData(dataSetId, mediumType, accessToken, columns, page, rows);
            if (data == null) {
                logger.error("failed to get data");
                recall(taskVo.setStatus(500), accessToken);
                return;
            }
            logger.info("get value success");
            for (Map<String, Object> column : columns) {
                if (!column.containsKey("mode")) {
                    continue;
                }
                List<String> values = (List<String>) column.get("values");
                String mode = (String) column.get("mode");
                String algorithm = (String) column.get("algorithm");
                //JSONArray args = (JSONArray) column.get("args");
                //Object[] objects = new Object[args.size()];
                //for (int i = 0; i < args.size(); i++) {
                //    objects[i] = args.get(i);
                //}
                logger.info("algorithm " + algorithm + " mode " + mode);
                //多态+反射执行单列脱敏
                try {
                    Class<?> aClass = SysUtils.getClazz(algorithm);
                    //Class<?> aClass = null;
                    if (aClass == null || StringUtils.isBlank(mode)) {
                        logger.info("class null or mode null");
                        column.put("values", values);
                        continue;
                    }
                    logger.info("class " + aClass.getName());
                    BaseAlgorithm baseAlgorithm = (BaseAlgorithm) aClass.newInstance();
                    values = baseAlgorithm.desensitive(values, mode);
                    column.put("values", values);
                } catch (Throwable e) {
                    e.printStackTrace();
                    logger.error("fail");
                    recall(taskVo.setStatus(500), accessToken);
                    return;
                }
            }
            //将列数组转为行数组
            List<Map<Object, String>> valuesToInsert = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                Map<Object, String> row = new LinkedHashMap<>();
                for (Map<String, Object> column : columns) {
                    List<String> values = (List<String>) column.get("values");
                    Object columnName = column.get("columnName");
                    if (values.size() > i) {
                        row.put(columnName, values.get(i));
                    } else {
                        row.put(columnName, null);
                    }

                }
                valuesToInsert.add(row);
            }
            saveData(valuesToInsert, dataSetId, sinkTableName, mediumType, accessToken);
            total -= rows;
            page++;
        }
        recall(taskVo.setStatus(200), accessToken);
    }

    private Map<String, Object> getData(String dataSetId, String mediumType, String accessToken, List<Map<String, Object>> columns, int page, int size) {
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        headers.add("accessToken", accessToken);
        Map<String, Object> parameter = new HashMap<>();
        parameter.put("dataSetId", dataSetId);
        parameter.put("mediumType", mediumType);
        parameter.put("pagenum", page);
        parameter.put("pagesize", size);
        String jsonObj = JSONObject.toJSONString(parameter);
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonObj, headers);
        ResponseEntity<String> response = restTemplate.exchange("http://" + daasUri + "/api/v2/daas/meta/dataQuality/getDataByDataSetId", HttpMethod.POST, requestEntity, String.class);
        Map map = JSON.parseObject(response.getBody(), Map.class);
        if ((int) map.get("code") == 200) {
            Map<String, Object> data = (Map<String, Object>) ((Map<String, Object>) map.get("result")).get("data");
            List<Map> datas = (List<Map>) data.get("datas");
            if (datas.size() > 0) {
                Map<String, Object> result = new HashMap<>();
                result.put("total", data.get("totalNum"));
                //result.put("sinkTableName", data.get("sinkTableName"));
                result.put("columnInfos", columns);
                if (columns.size() == 0) {
                    for (String field : (List<String>) data.get("fields")) {
                        Map<String, Object> column = new HashMap<>();
                        column.put("columnName", field);
                        column.put("values", new ArrayList<>());
                        columns.add(column);
                    }
                    /*for (Object key : datas.get(0).keySet()) {
                        Map<String, Object> column = new HashMap<>();
                        column.put("columnName", key);
                        column.put("values", new ArrayList<>());
                        columns.add(column);
                    }*/
                } else {
                    for (Map<String, Object> column : columns) {
                        column.put("values", new ArrayList<>());
                    }
                }
                for (Map row : datas) {
                    for (Map<String, Object> column : columns) {
                        Object value = row.get(column.get("columnName"));
                        List<String> values = (List<String>) column.get("values");
                        if (value == null) {
                            values.add(null);
                        } else {
                            values.add(value.toString());
                        }
                    }
                }
                return result;
            }
        }
        return null;
    }

    private void match(List<Map<String, Object>> columns) {
        List<Algorithm> algorithms = algorithmRepository.findAllByOriginalTrueOrderByPriorityAsc();
        for (Map<String, Object> column : columns) {
            List<String> values = (List<String>) column.get("values");
            for (Algorithm algorithm : algorithms) {
                String className = PACKAGE + algorithm.getClassName();
                boolean match;
                try {
                    Class<?> aClass = Class.forName(className);
                    BaseAlgorithm baseAlgorithm = (BaseAlgorithm) aClass.newInstance();
                    match = baseAlgorithm.match(values);
                } catch (Exception e) {
                    return;
                }
                if (match) {
                    column.put("algorithm", algorithm.getUuid());
                    break;
                }
            }
        }
    }

    private void saveData(List<Map<Object, String>> datas, String dataSetId, String sinkTableName, String mediumType, String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        headers.add("accessToken", accessToken);
        Map<String, Object> parameter = new HashMap<>();
        parameter.put("dataSetId", dataSetId);
        parameter.put("sinkTableName", sinkTableName);
        parameter.put("mediumType", mediumType);
        parameter.put("desensitizationData", datas);
        String jsonObj = JSONObject.toJSONString(parameter);
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonObj, headers);
        ResponseEntity<String> response = restTemplate.exchange("http://" + daasUri + "/api/v2/daas/meta/dataQuality/saveData", HttpMethod.POST, requestEntity, String.class);
        logger.info(response.getBody());
    }

    private String getSinkTableName(String dataSetId, String mediumType, String taskId, String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        headers.add("accessToken", accessToken);
        Map<String, Object> parameter = new HashMap<>();
        parameter.put("dataSetId", dataSetId);
        parameter.put("mediumType", mediumType);
        parameter.put("taskId", taskId);
        String jsonObj = JSONObject.toJSONString(parameter);
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonObj, headers);
        ResponseEntity<String> response = restTemplate.exchange("http://" + daasUri + "/api/v2/daas/meta/dataQuality/getSinkTable", HttpMethod.POST, requestEntity, String.class);
        logger.info(response.getBody());
        Map map = JSON.parseObject(response.getBody(), Map.class);
        return (String) ((Map) ((Map) map.get("result")).get("data")).get("sinkTableName");
    }

    private void recall(TaskVo taskVo, String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        headers.add("accessToken", accessToken);
        String jsonObj = JSONObject.toJSONString(taskVo);
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonObj, headers);
        ResponseEntity<String> response = restTemplate.exchange("http://" + daasUri + "/api/v2/daas/meta/dataQuality/getStatus", HttpMethod.POST, requestEntity, String.class);
        logger.info(response.getBody());
    }
}