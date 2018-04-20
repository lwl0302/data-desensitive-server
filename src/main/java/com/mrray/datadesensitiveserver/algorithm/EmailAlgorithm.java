package com.mrray.datadesensitiveserver.algorithm;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EmailAlgorithm implements BaseAlgorithm {
    private static final String REGEX = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";

    @Override
    public boolean match(List<String> values) {
        int count = 0;
        Set<String> matched = new HashSet<>();
        for (String s : values) {
            if (StringUtils.isBlank(s)) {
                continue;
            }
            if (s.contains("@") && s.matches(REGEX)) {
                count++;
                matched.add(s);
            }
        }
        int nonRepetitive = matched.size();
        return nonRepetitive >= 100 || count > 0.5 * values.size();
    }

    public List<String> emailone(List<String> values, String mask) {
        List<String> result = new ArrayList<>();
        for (String value : values) {
            if (StringUtils.isBlank(value)) {
                result.add(value);
                continue;
            }
            if (value.contains("@")) {
                int index = value.indexOf('@');
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < index; i++) {
                    stringBuilder.append(mask);
                }
                stringBuilder.append(value.substring(index));
                value = stringBuilder.toString();
            }
            result.add(value);
        }
        return result;
    }
}