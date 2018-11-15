package com.mrray.datadesensitiveserver.algorithm;

import com.mrray.datadesensitiveserver.utils.SysUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BankcardAlgorithm implements BaseAlgorithm {
    @Override
    public boolean match(List<String> values) {
        int count = 0;
        Set<String> matched = new HashSet<>();
        for (String s : values) {
            if (StringUtils.isBlank(s)) {
                continue;
            }
            int length = s.length();
            if (length >= 15 && length < 22 && checkBankCard(s)) {
                count++;
                matched.add(s);
            }
        }
        int nonRepetitive = matched.size();
        return nonRepetitive >= 100 || count > 0.5 * values.size();
    }

    private static boolean checkBankCard(String cardId) {
        char bit = getBankCardCheckCode(cardId.substring(0, cardId.length() - 1));
        return bit != 'N' && cardId.charAt(cardId.length() - 1) == bit;
    }

    private static char getBankCardCheckCode(String nonCheckCodeCardId) {
        if (nonCheckCodeCardId == null || nonCheckCodeCardId.trim().length() == 0
                || !nonCheckCodeCardId.matches("\\d+")) {
            return 'N';
        }
        char[] chs = nonCheckCodeCardId.trim().toCharArray();
        int luhmSum = 0;
        for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
            int k = chs[i] - '0';
            if (j % 2 == 0) {
                k *= 2;
                k = k / 10 + k % 10;
            }
            luhmSum += k;
        }
        return (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
    }

    public List<String> bankcardone(List<String> values) {
        List<String> result = new ArrayList<>();
        for (String value : values) {
            if (StringUtils.isBlank(value)) {
                result.add(value);
                continue;
            }
            int length = value.length();
            if (length == 16) {
                value = value.substring(0, 6) + SysUtils.buildMask("*", 10);
            } else if (length == 19) {
                value = value.substring(0, 6) + SysUtils.buildMask("*", 13);
            }
            result.add(value);
        }
        return result;
    }

    public List<String> bankcardtwo(List<String> values) {
        List<String> result = new ArrayList<>();
        for (String value : values) {
            if (StringUtils.isBlank(value)) {
                result.add(value);
                continue;
            }
            int length = value.length();
            if (length == 16) {
                value = RandomStringUtils.random(16, false, true);
            } else if (length == 19) {
                value = RandomStringUtils.random(19, false, true);
            }
            result.add(value);
        }
        return result;
    }
}