package com.mrray.datadesensitiveserver.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mrray.datadesensitiveserver.entity.domain.Algorithm;
import com.mrray.datadesensitiveserver.entity.domain.Mode;
import com.mrray.datadesensitiveserver.entity.dto.PageQueryDto;
import com.mrray.datadesensitiveserver.entity.vo.AlgorithmVo;
import com.mrray.datadesensitiveserver.entity.vo.PageQueryVo;
import com.mrray.datadesensitiveserver.entity.vo.RestResponseBody;
import com.mrray.datadesensitiveserver.repository.AlgorithmRepository;
import com.mrray.datadesensitiveserver.repository.ModeRepository;
import com.mrray.datadesensitiveserver.service.AlgorithmService;
import com.mrray.datadesensitiveserver.utils.SysUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AlgorithmServiceImpl implements AlgorithmService {
    private final AlgorithmRepository algorithmRepository;
    private static Logger logger = LoggerFactory.getLogger("AlgorithmServiceImpl");
    private final ModeRepository modeRepository;

    @Autowired
    public AlgorithmServiceImpl(AlgorithmRepository algorithmRepository, ModeRepository modeRepository) {
        this.algorithmRepository = algorithmRepository;
        this.modeRepository = modeRepository;
    }

    @Override
    public RestResponseBody getAlgorithmList(PageQueryDto pageQueryDto, boolean all) {
        PageQueryVo<AlgorithmVo> pageQueryVo = new PageQueryVo<>();
        pageQueryDto.setDirection("ASC");
        Pageable page = new PageRequest(pageQueryDto.getPage() - 1, pageQueryDto.getSize(), Sort.Direction.fromString(pageQueryDto.getDirection()), pageQueryDto.getProperty());
        Page<Algorithm> algorithms = algorithmRepository.findAll(page);
        SysUtils.mapperPageInfoToVo(pageQueryDto, algorithms, pageQueryVo);
        for (Algorithm algorithm : algorithms) {
            if (!(all || algorithm.getOriginal())) {
                continue;
            }
            AlgorithmVo algorithmVo = new AlgorithmVo();
            BeanUtils.copyProperties(algorithm, algorithmVo);
            List<Map<String, Object>> list = new ArrayList<>();
            for (Mode mode : algorithm.getModes()) {
                Map<String, Object> map = new HashMap<>();
                map.put("uuid", mode.getUuid());
                if (!all && "手动设置时间".equals(mode.getDescription())) {
                    map.put("discription", "替换为当前时间");
                } else {
                    map.put("discription", mode.getDescription());
                }
                list.add(map);
            }
            algorithmVo.setModes(list);
            pageQueryVo.getContent().add(algorithmVo);
        }
        return new RestResponseBody<>().setData(pageQueryVo);
    }

    @Override
    public RestResponseBody load(MultipartFile file) {
        RestResponseBody restResponseBody = new RestResponseBody();
        String path = System.getProperty("user.dir") + "/algorithms";
        File dir = new File(path);
        if (!dir.exists()) {
            boolean mkdirs = dir.mkdirs();
            if (!mkdirs) {
                return restResponseBody.setError("can't mkdirs");
            }
        }
        String filename = file.getOriginalFilename();
        String algorithmName = filename.substring(0, filename.indexOf("."));
        String filePath = path + "/" + filename;
        File jar = new File(filePath);
        try (FileOutputStream outputStream = new FileOutputStream(jar);
             InputStream inputStream = file.getInputStream()) {
            if (!jar.exists()) {
                boolean newFile = jar.createNewFile();
                if (!newFile) {
                    return restResponseBody.setError("can't create file");
                }
            }
            byte[] bytes = new byte[inputStream.available()];
            int read = inputStream.read(bytes);
            System.out.println("load file length : " + read);
            outputStream.write(bytes);
        } catch (Exception e) {
            logger.error("failed to load " + filename);
            logger.error(e.getMessage());
            restResponseBody.setMessage(e.getMessage());
            restResponseBody.setError("fail");
        }
        String tempJar = SysUtils.createTempJar(algorithmName, false);
        if (tempJar != null) {
            try (InputStream jarInputStream = SysUtils.getJarInputStream(tempJar, "init");
                 InputStreamReader inputStreamReader = new InputStreamReader(jarInputStream);
                 BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                String info = bufferedReader.readLine();
                Map<String, Object> infoMap = (Map<String, Object>) JSONObject.parse(info);
                String className = (String) infoMap.get("className");
                Algorithm algorithm = algorithmRepository.findByClassName(className);
                if (algorithm == null) {
                    algorithm = new Algorithm();
                }
                algorithm.setOriginal(false);
                algorithm.setClassName(className);
                algorithm.setDescription((String) infoMap.get("description"));
                algorithm.setPriority((int) algorithmRepository.count() + 1);
                algorithm.setName((String) infoMap.get("name"));
                algorithmRepository.save(algorithm);
                for (Object o : ((JSONArray) infoMap.get("modes"))) {
                    Map<String, Object> o1 = (Map<String, Object>) o;
                    String methodName = (String) o1.get("methodName");
                    Mode mode = modeRepository.findByMethodNameAndAlgorithm_Uuid(methodName, algorithm.getUuid());
                    if (mode == null) {
                        mode = new Mode();
                    }
                    mode.setMethodName(methodName);
                    mode.setDescription((String) o1.get("description"));
                    mode.setAlgorithm(algorithm);
                    modeRepository.save(mode);
                }
                bufferedReader.close();
                inputStreamReader.close();
                jarInputStream.close();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                restResponseBody.setMessage(e.getMessage());
                restResponseBody.setError("fail");
            }
        } else {
            restResponseBody.setError("算法插件包损坏或序列号不匹配");
        }
        return restResponseBody;
    }

    @Override
    public RestResponseBody serial() {
        return new RestResponseBody<>().setData(SysUtils.serial());
    }
}