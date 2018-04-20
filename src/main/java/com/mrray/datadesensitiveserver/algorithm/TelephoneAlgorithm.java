package com.mrray.datadesensitiveserver.algorithm;

import com.mrray.datadesensitiveserver.utils.SysUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TelephoneAlgorithm implements BaseAlgorithm {
    @Override
    public boolean match(List<String> values) {
        int count = 0;
        Set<String> matched = new HashSet<>();
        for (String s : values) {
            if (StringUtils.isBlank(s)) {
                continue;
            }
            if (s.matches("^(0[0-9]{2,3}-)?([2-9][0-9]{7})+(-[0-9]{1,4})?$")) {
                count++;
                matched.add(s);
            }
        }
        int nonRepetitive = matched.size();
        return nonRepetitive >= 100 || count > 0.5 * values.size();
    }

    public List<String> telephoneone(List<String> values, String mask) {
        List<String> result = new ArrayList<>();
        String x = SysUtils.buildMask(mask, 8);
        for (String value : values) {
            if (StringUtils.isBlank(value)) {
                result.add(value);
                continue;
            }
            int length = value.length();
            if (length == 12) {
                value = value.substring(0, 4) + x;
            } else if (length == 8) {
                value = x;
            } else if (length == 13) {
                value = value.substring(0, 5) + x;
            }
            result.add(value);
        }
        return result;
    }

    public List<String> telephonetwo(List<String> values, String mask) {
        List<String> result = new ArrayList<>();
        String x = SysUtils.buildMask(mask, 6);
        for (String value : values) {
            if (StringUtils.isBlank(value)) {
                result.add(value);
                continue;
            }
            int length = value.length();
            if (length == 12) {
                value = value.substring(0, 6) + x;
            } else if (length == 8) {
                value = value.substring(0, 2) + x;
            } else if (length == 13) {
                value = value.substring(0, 7) + x;
            }
            result.add(value);
        }
        return result;
    }

    public List<String> telephonethree(List<String> values) {
        List<String> result = new ArrayList<>();
        for (String value : values) {
            if (StringUtils.isBlank(value)) {
                result.add(value);
                continue;
            }
            int length = value.length();
            if (length == 12) {
                value = RandomStringUtils.random(3, false, true) + "-" + RandomStringUtils.random(8, false, true);
            } else if (length == 8) {
                value = RandomStringUtils.random(8, false, true);
            } else if (length == 13) {
                value = RandomStringUtils.random(4, false, true) + "-" + RandomStringUtils.random(8, false, true);
            }
            result.add(value);
        }
        return result;
    }
}