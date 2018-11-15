package com.mrray.datadesensitiveserver.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.mrray.datadesensitiveserver.entity.domain.Algorithm;
import com.mrray.datadesensitiveserver.entity.domain.Mode;
import com.mrray.datadesensitiveserver.entity.domain.Rule;
import com.mrray.datadesensitiveserver.entity.dto.RuleDto;
import com.mrray.datadesensitiveserver.entity.dto.RuleQueryDto;
import com.mrray.datadesensitiveserver.entity.vo.PageQueryVo;
import com.mrray.datadesensitiveserver.entity.vo.RestResponseBody;
import com.mrray.datadesensitiveserver.entity.vo.RuleVo;
import com.mrray.datadesensitiveserver.repository.AlgorithmRepository;
import com.mrray.datadesensitiveserver.repository.ModeRepository;
import com.mrray.datadesensitiveserver.repository.RuleRepository;
import com.mrray.datadesensitiveserver.service.RuleService;
import com.mrray.datadesensitiveserver.utils.SysUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

@Service
public class RuleServiceImpl implements RuleService {
    private final RuleRepository ruleRepository;
    private final ModeRepository modeRepository;
    private final AlgorithmRepository algorithmRepository;
    private static final List<String> METHODS = Arrays.asList(
            "IDone", "IDtwo", "IDthree",
            "emailone",
            "cellphoneone", "cellphonetwo",
            "bankcardone",
            "addressone", "addresstwo",
            "telephoneone", "telephonetwo",
            "socialCreditone", "socialCredittwo",
            "datetwo",
            "nameone", "nametwo",
            "stringone", "stringtwo", "stringthree", "stringfour");

    @Autowired
    public RuleServiceImpl(RuleRepository ruleRepository, ModeRepository modeRepository, AlgorithmRepository algorithmRepository) {
        this.ruleRepository = ruleRepository;
        this.modeRepository = modeRepository;
        this.algorithmRepository = algorithmRepository;
    }

    @Override
    public RestResponseBody getRuleList(RuleQueryDto ruleQueryDto) {
        PageQueryVo<RuleVo> pageQueryVo = new PageQueryVo<>();
        Pageable page = new PageRequest(ruleQueryDto.getPage() - 1, ruleQueryDto.getSize(), Sort.Direction.fromString(ruleQueryDto.getDirection()), ruleQueryDto.getProperty());
        Page<Rule> rules = ruleRepository.findAll((Root<Rule> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            Predicate predicate = root.isNotNull();
            String name = ruleQueryDto.getName();
            if (StringUtils.isNotBlank(name)) {
                predicate = cb.and(predicate, cb.like(root.get("name"), "%" + name + "%"));
            }
            predicate = cb.and(predicate, cb.equal(root.get("temporary"), false));
            return predicate;
        }, page);
        SysUtils.mapperPageInfoToVo(ruleQueryDto, rules, pageQueryVo);
        for (Rule rule : rules) {
            Map<String, String> parse = (Map<String, String>) JSONArray.parse(rule.getDetails());
            Collection<String> values = parse.values();
            List<Map<String, String>> details = new ArrayList<>();
            for (Object value : values) {
                Map<String, String> map = new HashMap<>();
                String uuid = (String) value;
                if (uuid.length() != 8) {
                    continue;
                }
                Mode mode = modeRepository.findByUuid(uuid);
                Algorithm algorithm = mode.getAlgorithm();
                map.put("name", algorithm.getName());
                map.put("mode", mode.getDescription());
                map.put("uuid", mode.getUuid());
                map.put("args", parse.getOrDefault(algorithm.getUuid() + "arg", ""));
                details.add(map);
            }
            RuleVo ruleVo = new RuleVo();
            BeanUtils.copyProperties(rule, ruleVo);
            ruleVo.setDetails(details);
            pageQueryVo.getContent().add(ruleVo);
        }
        return new RestResponseBody<>().setData(pageQueryVo);
    }

    //@Override
    //public RestResponseBody postRule(RuleDto ruleDto) {
    //    RestResponseBody<Map<String, String>> restResponseBody = new RestResponseBody<>();
    //    Rule rule = new Rule();
    //    BeanUtils.copyProperties(ruleDto, rule);
    //    List<String> modes = ruleDto.getModes();
    //    List<String> args = ruleDto.getArgs();
    //    Map<String, String> checkModes = checkModes(modes, args);
    //    if (!ruleDto.getTemporary() && ruleRepository.findByName(ruleDto.getName()).size() > 0) {
    //        return restResponseBody.setError("Incorrect name value !");
    //    }
    //    if (checkModes == null) {
    //        return restResponseBody.setError("Incorrect mode values !");
    //    }
    //    rule.setDetails(JSONObject.toJSONString(checkModes));
    //    SysUtils.save(ruleRepository, rule);
    //    Map<String, String> map = new HashMap<>();
    //    map.put("uuid", rule.getUuid());
    //    return restResponseBody.setData(map);
    //}

    @Override
    public RestResponseBody putRule(String uuid, RuleDto ruleDto) {
        RestResponseBody restResponseBody = new RestResponseBody<>();
       /* Rule rule = ruleRepository.findByUuid(uuid);
        if (rule == null) {
            return restResponseBody.setError("此策略已被删除，请刷新页面后重试");
        }
        BeanUtils.copyProperties(ruleDto, rule);
        List<String> modes = ruleDto.getModes();
        List<String> args = ruleDto.getArgs();
        Map<String, String> checkModes = checkModes(modes, args);
        List<Rule> record = ruleRepository.findByName(ruleDto.getName());
        if (!ruleDto.getTemporary() && record.size() > 0 && !record.get(0).getUuid().equals(uuid)) {
            return restResponseBody.setError("Incorrect name value !");
        }
        if (checkModes == null) {
            return restResponseBody.setError("Incorrect mode values !");
        }
        rule.setDetails(JSONObject.toJSONString(checkModes));
        SysUtils.save(ruleRepository, rule);*/
        return restResponseBody;
    }

    @Override
    public RestResponseBody deleteRule(String uuid) {
        RestResponseBody restResponseBody = new RestResponseBody<>();
        Integer delete = ruleRepository.deleteByUuid(uuid);
        if (delete == 0) return restResponseBody.setError("Task uuid is not found !");
        return restResponseBody;
    }

   /* private Map<String, String> checkModes(List<String> modes, List<String> args) {
        Map<String, String> map = new HashMap<>();
        Set<String> set = new HashSet<>();
        for (int i = 0; i < modes.size(); i++) {
            String muuid = modes.get(i);
            Mode mode = modeRepository.findByUuid(muuid);
            if (mode != null) {
                String auuid = mode.getAlgorithm().getUuid();
                set.add(auuid);
                map.put(auuid, muuid);
                if (METHODS.contains(mode.getMethodName())) {
                    map.put(auuid + "arg", args.get(i));
                }
            }
        }
        List<Algorithm> all = algorithmRepository.findAllByOriginalTrueOrderByPriorityAsc();
        int size = all.size();
        if (set.size() == size && modes.size() == size) {
            return map;
        } else {
            return null;
        }
    }*/
}