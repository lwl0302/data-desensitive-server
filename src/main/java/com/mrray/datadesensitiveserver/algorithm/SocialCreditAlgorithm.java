package com.mrray.datadesensitiveserver.algorithm;

import com.mrray.datadesensitiveserver.utils.SysUtils;
import org.apache.commons.lang.StringUtils;

import java.util.*;

public class SocialCreditAlgorithm implements BaseAlgorithm {
    @Override
    public boolean match(List<String> values) {
        int count = 0;
        Set<String> matched = new HashSet<>();
        for (String s : values) {
            if (StringUtils.isBlank(s)) {
                continue;
            }
            int length = s.length();
            if (length == 18 && s.matches("^[A-Za-z0-9]+$") && s.equalsIgnoreCase(getValidCode(s.substring(0, 16).toUpperCase()))) {
                count++;
                matched.add(s);
            }
        }
        int nonRepetitive = matched.size();
        return nonRepetitive >= 100 || count > 0.5 * values.size();
    }

    private static String getValidCode(String str) {
        if (str.length() == 16) {
            String string = str.substring(8, str.length());
            String num17 = getNum17(string);
            str += num17;
            String num18 = getNum18(str);
            str += num18;
        }
        return str;
    }

    private static String getNum17(String str) {
        int[] w = {3, 7, 9, 10, 5, 8, 4, 2};//8位加权因子数组 , 传递进来的字符串参数为8位 , 从0到7分别代表一个整型值
        //该集合存放熊0-9,A-Z所对应的整型值
        Map<String, Integer> machineNumMap = new LinkedHashMap<>();
        for (int i = 0; i < 10; i++) {
            machineNumMap.put(String.valueOf(i), i);
        }
        String ascii = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        char[] bigWord = ascii.toCharArray();
        int num = 10;
        for (char c : bigWord) {
            machineNumMap.put(String.valueOf(c), num++);
        }
        //算法开始计算 , 获取总和
        char[] charArray = str.toCharArray();
        int sum = 0;
        for (int i = 0; i < charArray.length; i++) {
            sum += w[i] * machineNumMap.get(String.valueOf(charArray[i]));
        }
        //当总和除以11余数为1时第9位校验码为X , 余数为0是校验码为0
        int nine = sum % 11;
        String nineStr;
        if (nine == 1) {
            nineStr = "X";
        } else if (nine == 0) {
            nineStr = "0";
        } else {
            nineStr = String.valueOf(11 - nine);
        }
        return nineStr;
    }

    private static String getNum18(String str) {
        int[] w = {1, 2, 9, 27, 19, 26, 16, 17, 20, 29, 25, 13, 8, 24, 10, 30, 28};//17位加权因子数组 , 传递进来的字符串参数为17位 , 从0到16分别代表一个整型值
        // 该集合存放熊0-9,A-Z所对应的整型值
        Map<String, Integer> machineNumMap = new LinkedHashMap<>();
        for (int i = 0; i < 10; i++) {
            machineNumMap.put(String.valueOf(i), i);
        }
        String ascii = "ABCDEFGHJKLMNPQRTUWXY";//没有IOZSV
        char[] bigWord = ascii.toCharArray();
        int num = 10;
        for (char c : bigWord) {
            machineNumMap.put(String.valueOf(c), num++);
        }
        // 算法开始计算 , 获取总和
        char[] charArray = str.toCharArray();
        int sum = 0;

        for (int i = 0; i < charArray.length; i++) {
            Integer integer = machineNumMap.get(String.valueOf(charArray[i]));
            if (integer == null) {
                break;
            }
            sum += w[i] * integer;
        }
        //当总和除以31余数为1时第9位校验码为Y , 余数为0是校验码为0
        int eightteen = sum % 31;
        String eighteenStr = "";
        if (eightteen == 1) {
            eighteenStr = "Y";
        } else if (eightteen == 0) {
            eighteenStr = "0";
        } else {
            for (Map.Entry<String, Integer> entry : machineNumMap.entrySet()) {
                if (entry.getValue() == 31 - eightteen - 1) {
                    eighteenStr = entry.getKey();
                }
            }
        }
        return eighteenStr;
    }

    public List<String> socialCreditone(List<String> values) {
        List<String> result = new ArrayList<>();
        for (String value : values) {
            if (StringUtils.isBlank(value)) {
                result.add(value);
                continue;
            }
            int length = value.length();
            if (length == 18) {
                value = value.substring(0, 8) + SysUtils.buildMask("*", 9) + value.substring(17);
            }
            result.add(value);
        }
        return result;
    }

    public List<String> socialCredittwo(List<String> values) {
        List<String> result = new ArrayList<>();
        for (String value : values) {
            if (StringUtils.isBlank(value)) {
                result.add(value);
                continue;
            }
            int length = value.length();
            if (length == 18) {
                value = value.substring(0, 2) + SysUtils.buildMask("*", 15) + value.substring(17);
            }
            result.add(value);
        }
        return result;
    }
}