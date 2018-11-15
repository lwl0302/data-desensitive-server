package com.mrray.datadesensitiveserver.algorithm;

import com.mrray.datadesensitiveserver.utils.SysUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;

import java.util.*;

public class CellphoneAlgorithm implements BaseAlgorithm {
    private static final List<String> SEGMENTS = Arrays.asList(
            //移动号段
            "139", "138", "137", "136", "135", "134", "147", "150", "151", "152", "157", "158", "159", "178", "182", "183", "184", "187", "188",
            //联通号段
            "130", "131", "132", "155", "156", "185", "186", "145", "176",
            //电信号段
            "133", "153", "177", "173", "180", "181", "189",
            //虚拟运营商号段
            "170", "171"
    );

    @Override
    public boolean match(List<String> values) {
        int count = 0;
        Set<String> matched = new HashSet<>();
        for (String s : values) {
            if (StringUtils.isBlank(s)) {
                continue;
            }
            int length = s.length();
            if (length > 10 && length < 16 && SEGMENTS.contains(s.substring(length - 11, length - 8))) {
                count++;
                matched.add(s);
            }
        }
        int nonRepetitive = matched.size();
        return nonRepetitive >= 100 || count > 0.5 * values.size();
    }

    public List<String> cellphoneone(List<String> values) {
        List<String> result = new ArrayList<>();
        for (String value : values) {
            if (StringUtils.isBlank(value)) {
                result.add(value);
                continue;
            }
            if (value.length() == 11) {
                value = value.substring(0, 3) + SysUtils.buildMask("*", 4) + value.substring(7, 11);
            }
            result.add(value);
        }
        return result;
    }

    public List<String> cellphonetwo(List<String> values) {
        List<String> result = new ArrayList<>();
        for (String value : values) {
            if (StringUtils.isBlank(value)) {
                result.add(value);
                continue;
            }
            if (value.length() == 11) {
                value = value.substring(0, 7) + SysUtils.buildMask("*", 4);
            }
            result.add(value);
        }
        return result;
    }

    public List<String> cellphonethree(List<String> values) {
        List<String> result = new ArrayList<>();
        for (String value : values) {
            if (StringUtils.isBlank(value)) {
                result.add(value);
                continue;
            }
            if (value.length() == 11) {
                value = value.substring(0, 3) + RandomStringUtils.random(8, false, true);
            }
            result.add(value);
        }
        return result;
    }
}