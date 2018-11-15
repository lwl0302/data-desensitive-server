package com.mrray.datadesensitiveserver.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mrray.datadesensitiveserver.entity.domain.Algorithm;
import com.mrray.datadesensitiveserver.entity.domain.Mode;
import com.mrray.datadesensitiveserver.entity.vo.AlgorithmVo;
import com.mrray.datadesensitiveserver.entity.vo.RestResponseBody;
import com.mrray.datadesensitiveserver.repository.AlgorithmRepository;
import com.mrray.datadesensitiveserver.repository.ModeRepository;
import com.mrray.datadesensitiveserver.service.AlgorithmService;
import com.mrray.datadesensitiveserver.utils.SysUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.*;

@Service
public class AlgorithmServiceImpl implements AlgorithmService {
    private final AlgorithmRepository algorithmRepository;
    private static Logger logger = LoggerFactory.getLogger("AlgorithmServiceImpl");
    private final ModeRepository modeRepository;
    private static final List<String> algorithms = Arrays.asList("银行卡", "手机号", "时间", "身份证", "姓名", "固定电话", "地址", "信用代码", "邮箱地址");
    private static final List<String> classNames = Arrays.asList("BankcardAlgorithm", "CellphoneAlgorithm", "DateAlgorithm", "IDAlgorithm", "NameAlgorithm", "TelephoneAlgorithm", "AddressAlgorithm", "SocialCreditAlgorithm", "EmailAlgorithm");
    private static final List<String> uuids = Arrays.asList("c7mnxxai", "omxsmx3v", "13ajsety", "clnmpzch", "amhswml1", "cuygfzvt", "367i4pb5", "wd5blopo", "dsawqu34");
    private static final List<Integer> priorities = Arrays.asList(3, 4, 6, 1, 5, 7, 8, 2, 9);

    @PostConstruct
    public void initData() {
        if (algorithmRepository.findAll().size() == 9) {
            return;
        }
        for (int i = 0; i < algorithms.size(); i++) {
            Algorithm algorithm = new Algorithm();
            algorithm.setId((long) i + 1);
            algorithm.setClassName(classNames.get(i));
            algorithm.setName(algorithms.get(i));
            algorithm.setOriginal(true);
            algorithm.setPriority(priorities.get(i));
            algorithm.setUuid(uuids.get(i));
            List<Mode> modes = new ArrayList<>();
            algorithm.setModes(modes);
            if (i == 0) {
                Mode mode1 = new Mode();
                mode1.setAlgorithm(algorithm);
                mode1.setDescription("个人账号标识掩码");
                mode1.setMethodName("bankcardone");
                mode1.setUuid("wabbp61f");
                modes.add(mode1);
                Mode mode2 = new Mode();
                mode2.setAlgorithm(algorithm);
                mode2.setDescription("全部随机数");
                mode2.setMethodName("bankcardtwo");
                mode2.setUuid("rwsq55rb");
                modes.add(mode2);
                Mode mode3 = new Mode();
                mode3.setAlgorithm(algorithm);
                mode3.setDescription("不脱敏");
                mode3.setMethodName("");
                mode3.setUuid("2nhnb0xw");
                modes.add(mode3);
            } else if (i == 2) {
                Mode mode1 = new Mode();
                mode1.setAlgorithm(algorithm);
                mode1.setDescription("替换为固定时间");
                mode1.setMethodName("dateone");
                mode1.setUuid("n0alqkes");
                modes.add(mode1);
                Mode mode4 = new Mode();
                mode4.setAlgorithm(algorithm);
                mode4.setDescription("不脱敏");
                mode4.setMethodName("");
                mode4.setUuid("wvo013tm");
                modes.add(mode4);
            } else if (i == 1) {
                Mode mode1 = new Mode();
                mode1.setAlgorithm(algorithm);
                mode1.setDescription("中间四位掩码");
                mode1.setMethodName("cellphoneone");
                mode1.setUuid("30ku3vgx");
                modes.add(mode1);
                Mode mode2 = new Mode();
                mode2.setAlgorithm(algorithm);
                mode2.setDescription("最后四位掩码");
                mode2.setMethodName("cellphonetwo");
                mode2.setUuid("yk1uqush");
                modes.add(mode2);
                Mode mode3 = new Mode();
                mode3.setAlgorithm(algorithm);
                mode3.setDescription("后八位随机数");
                mode3.setMethodName("cellphonethree");
                mode3.setUuid("btohhr8u");
                modes.add(mode3);
                Mode mode4 = new Mode();
                mode4.setAlgorithm(algorithm);
                mode4.setDescription("不脱敏");
                mode4.setMethodName("");
                mode4.setUuid("zrgwgxuf");
                modes.add(mode4);
            } else if (i == 3) {
                Mode mode1 = new Mode();
                mode1.setAlgorithm(algorithm);
                mode1.setDescription("保留地址识别码");
                mode1.setMethodName("IDone");
                mode1.setUuid("ywya6n8o");
                modes.add(mode1);
                Mode mode2 = new Mode();
                mode2.setAlgorithm(algorithm);
                mode2.setDescription("保留出生日期识别码");
                mode2.setMethodName("IDtwo");
                mode2.setUuid("4m26wwjr");
                modes.add(mode2);
                Mode mode3 = new Mode();
                mode3.setAlgorithm(algorithm);
                mode3.setDescription("保留地址及出生日期识别码");
                mode3.setMethodName("IDthree");
                mode3.setUuid("rpchl7ym");
                modes.add(mode3);
                Mode mode5 = new Mode();
                mode5.setAlgorithm(algorithm);
                mode5.setDescription("全部随机数");
                mode5.setMethodName("IDfour");
                mode5.setUuid("rpchl7ym");
                modes.add(mode3);
                Mode mode4 = new Mode();
                mode4.setAlgorithm(algorithm);
                mode4.setDescription("不脱敏");
                mode4.setMethodName("");
                mode4.setUuid("n9g8fcjr");
                modes.add(mode4);
            } else if (i == 4) {
                Mode mode1 = new Mode();
                mode1.setAlgorithm(algorithm);
                mode1.setDescription("保留姓氏及姓名字数");
                mode1.setMethodName("nameone");
                mode1.setUuid("uot5qukm");
                modes.add(mode1);
                Mode mode2 = new Mode();
                mode2.setAlgorithm(algorithm);
                mode2.setDescription("保留姓氏但不保留字数");
                mode2.setMethodName("nametwo");
                mode2.setUuid("q54szj3p");
                modes.add(mode2);
                Mode mode4 = new Mode();
                mode4.setAlgorithm(algorithm);
                mode4.setDescription("不脱敏");
                mode4.setMethodName("");
                mode4.setUuid("1nfncoa7");
                modes.add(mode4);
            } else if (i == 5) {
                Mode mode1 = new Mode();
                mode1.setAlgorithm(algorithm);
                mode1.setDescription("保留区号");
                mode1.setMethodName("telephoneone");
                mode1.setUuid("ugge3eyf");
                modes.add(mode1);
                Mode mode2 = new Mode();
                mode2.setAlgorithm(algorithm);
                mode2.setDescription("保留区县号");
                mode2.setMethodName("telephonetwo");
                mode2.setUuid("vd2ci7y9");
                modes.add(mode2);
                Mode mode3 = new Mode();
                mode3.setAlgorithm(algorithm);
                mode3.setDescription("全部随机数");
                mode3.setMethodName("telephonethree");
                mode3.setUuid("mtxm98xc");
                modes.add(mode3);
                Mode mode4 = new Mode();
                mode4.setAlgorithm(algorithm);
                mode4.setDescription("不脱敏");
                mode4.setMethodName("");
                mode4.setUuid("zcybbpkt");
                modes.add(mode4);
            } else if (i == 6) {
                Mode mode1 = new Mode();
                mode1.setAlgorithm(algorithm);
                mode1.setDescription("保留省市");
                mode1.setMethodName("addressone");
                mode1.setUuid("pdbqpsuk");
                modes.add(mode1);
                Mode mode2 = new Mode();
                mode2.setAlgorithm(algorithm);
                mode2.setDescription("保留省市区");
                mode2.setMethodName("addresstwo");
                mode2.setUuid("03zgh1d7");
                modes.add(mode2);
                Mode mode4 = new Mode();
                mode4.setAlgorithm(algorithm);
                mode4.setDescription("不脱敏");
                mode4.setMethodName("");
                mode4.setUuid("5rcfuagw");
                modes.add(mode4);
            } else if (i == 7) {
                Mode mode1 = new Mode();
                mode1.setAlgorithm(algorithm);
                mode1.setDescription("组织机构代码掩码");
                mode1.setMethodName("socialCreditone");
                mode1.setUuid("h4j1nd6v");
                modes.add(mode1);
                Mode mode2 = new Mode();
                mode2.setAlgorithm(algorithm);
                mode2.setDescription("税务登记证号码掩码");
                mode2.setMethodName("socialCredittwo");
                mode2.setUuid("ajc29g2q");
                modes.add(mode2);
                Mode mode4 = new Mode();
                mode4.setAlgorithm(algorithm);
                mode4.setDescription("不脱敏");
                mode4.setMethodName("");
                mode4.setUuid("7lurgzya");
                modes.add(mode4);
            } else if (i == 8) {
                Mode mode1 = new Mode();
                mode1.setAlgorithm(algorithm);
                mode1.setDescription("@前字符掩码");
                mode1.setMethodName("emailone");
                mode1.setUuid("g5rty6fe");
                modes.add(mode1);
                Mode mode4 = new Mode();
                mode4.setAlgorithm(algorithm);
                mode4.setDescription("不脱敏");
                mode4.setMethodName("");
                mode4.setUuid("vv3xy5fg");
                modes.add(mode4);
            }
            algorithmRepository.save(algorithm);
            modeRepository.save(modes);
        }
    }

    @Autowired
    public AlgorithmServiceImpl(AlgorithmRepository algorithmRepository, ModeRepository modeRepository) {
        this.algorithmRepository = algorithmRepository;
        this.modeRepository = modeRepository;
    }

    @Override
    public RestResponseBody getAlgorithmList() {
        List<AlgorithmVo> pageQueryVo = new ArrayList<>();
        List<Algorithm> algorithms = algorithmRepository.findAll();
        for (Algorithm algorithm : algorithms) {
            AlgorithmVo algorithmVo = new AlgorithmVo();
            BeanUtils.copyProperties(algorithm, algorithmVo);
            List<Map<String, Object>> list = new ArrayList<>();
            for (Mode mode : algorithm.getModes()) {
                Map<String, Object> map = new HashMap<>();
                map.put("uuid", mode.getUuid());
                if ("手动设置时间".equals(mode.getDescription())) {
                    map.put("discription", "替换为当前时间");
                } else {
                    map.put("discription", mode.getDescription());
                }
                list.add(map);
            }
            algorithmVo.setModes(list);
            pageQueryVo.add(algorithmVo);
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