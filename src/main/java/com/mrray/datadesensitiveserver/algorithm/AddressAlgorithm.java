package com.mrray.datadesensitiveserver.algorithm;

import org.apache.commons.lang.StringUtils;

import java.util.*;

public class AddressAlgorithm implements BaseAlgorithm {
    private static final List<String> SIGNS = Arrays.asList("省", "市", "县", "区", "镇", "州");
    private static final List<String> KEYWORDS = Arrays.asList("街", "村", "道", "路", "号", "楼", "单", "组", "座", "院", "园", "乡", "段", "环", "巷", "栋", "室");

    @Override
    public boolean match(List<String> values) {
        int count = 0;
        Set<String> matched = new HashSet<>();
        for (String s : values) {
            if (StringUtils.isBlank(s)) {
                continue;
            }
            int length = s.length();
            if (length > 2 && isAddress(s)) {
                count++;
                matched.add(s);
            }
        }
        int nonRepetitive = matched.size();
        return nonRepetitive >= 100 || count > 0.5 * values.size();
    }

    private static boolean isAddress(String s) {
        String[] split = s.split("");
        int count = 0;
        for (String c : split) {
            if (SIGNS.contains(c)) {
                count += 2;
            } else if (KEYWORDS.contains(c)) {
                count += 1;
            }
        }
        return count >= 3;
    }

    public List<String> addressone(List<String> values) {
        List<String> result = new ArrayList<>();
        for (String value : values) {
            if (StringUtils.isBlank(value)) {
                result.add(value);
                continue;
            }
            int c = value.indexOf('市');
            int p = value.indexOf('省');
            if (c != -1) {
                value = mask(value, c);
            } else if (p != -1) {
                value = mask(value, p);
            } else {
                value = mask(value, -1);
            }
            result.add(value);
        }
        return result;
    }

    public List<String> addresstwo(List<String> values) {
        List<String> result = new ArrayList<>();
        for (String value : values) {
            if (StringUtils.isBlank(value)) {
                result.add(value);
                continue;
            }
            int c = value.indexOf('市');
            int p = value.indexOf('省');
            int a = value.indexOf('区');
            if (a != -1) {
                value = mask(value, a);
            } else if (c != -1) {
                value = mask(value, c);
            } else if (p != -1) {
                value = mask(value, p);
            } else {
                value = mask(value, -1);
            }
            result.add(value);
        }
        return result;
    }

    private static String mask(String value, int p) {
        StringBuilder temp = new StringBuilder(value.substring(0, p + 1));
        for (int i = p + 1; i < value.length(); i++) {
            String s = Character.toString(value.charAt(i));
            if (SIGNS.contains(s) || KEYWORDS.contains(s)) {
                temp.append(s);
            } else {
                temp.append("*");
            }
        }
        return temp.toString();
    }
}