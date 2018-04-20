package com.mrray.datadesensitiveserver.algorithm;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class StringAlgorithm implements BaseAlgorithm {
    @Override
    public boolean match(List<String> values) {
        return false;
    }

    public List<String> stringone(List<String> values, double size, String mask) {
        List<String> result = new ArrayList<>();
        for (String value : values) {
            if (StringUtils.isBlank(value)) {
                result.add(value);
                continue;
            }
            value = remain(size, value, mask);
            result.add(value);
        }
        return result;
    }

    public List<String> stringtwo(List<String> values, double size, String mask) {
        List<String> result = new ArrayList<>();
        for (String value : values) {
            if (StringUtils.isBlank(value)) {
                result.add(value);
                continue;
            }
            StringBuilder stringBuilder = new StringBuilder(value);
            stringBuilder.reverse();
            stringBuilder = new StringBuilder(remain(size, stringBuilder.toString(), mask)).reverse();
            result.add(stringBuilder.toString());
        }
        return result;
    }

    public List<String> stringthree(List<String> values, double size, String mask) {
        List<String> result = new ArrayList<>();
        for (String value : values) {
            if (StringUtils.isBlank(value)) {
                result.add(value);
                continue;
            }
            value = replace(size, value, mask);
            result.add(value);
        }
        return result;
    }

    public List<String> stringfour(List<String> values, double size, String mask) {
        List<String> result = new ArrayList<>();
        for (String value : values) {
            if (StringUtils.isBlank(value)) {
                result.add(value);
                continue;
            }
            StringBuilder stringBuilder = new StringBuilder(value);
            stringBuilder.reverse();
            stringBuilder = new StringBuilder(replace(size, stringBuilder.toString(), mask)).reverse();
            result.add(stringBuilder.toString());
        }
        return result;
    }

    public List<String> stringfive(List<String> values, double size) {
        List<String> result = new ArrayList<>();
        for (String value : values) {
            if (StringUtils.isBlank(value)) {
                result.add(value);
                continue;
            }
            int length = value.length();
            if (length > size) {
                value = value.substring(0, (int) size);
            }
            result.add(value);
        }
        return result;
    }

    public List<String> stringsix(List<String> values, double size) {
        List<String> result = new ArrayList<>();
        for (String value : values) {
            if (StringUtils.isBlank(value)) {
                result.add(value);
                continue;
            }
            int length = value.length();
            if (length > size) {
                StringBuilder stringBuilder = new StringBuilder(value);
                value = new StringBuilder(stringBuilder.reverse().substring(0, (int) size)).reverse().toString();
            }
            result.add(value);
        }
        return result;
    }

    private static String remain(double size, String value, String mask) {
        int length = value.length();
        if (length > size) {
            StringBuilder stringBuilder = new StringBuilder(value.substring(0, (int) size));
            for (int i = 0; i < length - size; i++) {
                stringBuilder.append(mask);
            }
            value = stringBuilder.toString();
        }
        return value;
    }

    private static String replace(double size, String value, String mask) {
        int length = value.length();
        StringBuilder stringBuilder = new StringBuilder();
        if (length > size) {
            for (int i = 0; i < size; i++) {
                stringBuilder.append(mask);
            }
            stringBuilder.append(value.substring((int) size, length));
        } else {
            for (int i = 0; i < length; i++) {
                stringBuilder.append(mask);
            }
        }
        return stringBuilder.toString();
    }
}