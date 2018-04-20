package com.mrray.datadesensitiveserver.algorithm;

import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NumberAlgorithm implements BaseAlgorithm {
    @Override
    public boolean match(List<String> values) {
        return false;
    }

    public List<String> numberone(List<String> values, double replacement) {
        String s = BigDecimal.valueOf(replacement).toString();
        List<String> result = new ArrayList<>();
        for (String value : values) {
            if (StringUtils.isBlank(value)) {
                result.add(value);
                continue;
            }
            try {
                Double aDouble = Double.valueOf(value);
            } catch (Exception e) {
                result.add(value);
                continue;
            }
            result.add(s);
        }
        return result;
    }

    public List<String> numbertwo(List<String> values, double gradient, String mode) {
        List<String> result = new ArrayList<>();
        for (String value : values) {
            if (StringUtils.isBlank(value)) {
                result.add(value);
                continue;
            }
            double num;
            try {
                num = Double.valueOf(value);
            } catch (Exception e) {
                result.add(value);
                continue;
            }
            int quotient = (int) (num / gradient);
            double half = gradient / 2.0;
            double integer = quotient * gradient;
            double remainder = num - integer;
            if ("UP".equalsIgnoreCase(mode)) {
                if (num >= 0) {
                    num = integer + gradient;
                } else {
                    num = integer - gradient;
                }
            } else if ("DOWN".equalsIgnoreCase(mode)) {
                num = integer;
            } else if ("HALF_UP".equalsIgnoreCase(mode)) {
                if (num >= 0) {
                    if (remainder >= half) {
                        num = integer + gradient;
                    } else {
                        num = integer;
                    }
                } else {
                    if (remainder * (-1) >= half) {
                        num = integer - gradient;
                    } else {
                        num = integer;
                    }
                }
            }
            result.add(BigDecimal.valueOf(num).toString());
        }
        return result;
    }

    public List<String> numberthree(List<String> values, double min, double max) {
        if (max < min) {
            double temp;
            temp = max;
            max = min;
            min = temp;
        }
        Random random = new Random();
        List<String> result = new ArrayList<>();
        for (String value : values) {
            if (StringUtils.isBlank(value)) {
                result.add(value);
                continue;
            }
            try {
                Double aDouble = Double.valueOf(value);
            } catch (Exception e) {
                result.add(value);
                continue;
            }
            result.add((random.nextInt((int) max) % ((int) max - (int) min + 1) + (int) min) + "");
        }
        return result;
    }
}