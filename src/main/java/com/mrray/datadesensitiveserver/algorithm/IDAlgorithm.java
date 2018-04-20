package com.mrray.datadesensitiveserver.algorithm;

import com.mrray.datadesensitiveserver.utils.SysUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class IDAlgorithm implements BaseAlgorithm {
    private static final List<String> PROVINCES = Arrays.asList(
            "11", "12", "13", "14", "15",
            "21", "22", "23",
            "31", "32", "33", "34", "35", "36", "37",
            "41", "42", "43", "44", "45", "46",
            "50", "51", "52", "53", "54",
            "61", "62", "63", "64", "65",
            "71",
            "81", "82",
            "91"
    );
    private static final List<Integer> POWER = Arrays.asList(7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2);
    private static final List<String> VERIFYCODE = Arrays.asList("1", "0", "x", "9", "8", "7", "6", "5", "4", "3", "2");

    @Override
    public boolean match(List<String> values) {
        int count = 0;
        Set<String> matched = new HashSet<>();
        for (String s : values) {
            if (StringUtils.isBlank(s)) {
                continue;
            }
            if (s.length() == 15) {
                s = convert15to18(s);
            }
            if (s != null && s.length() == 18 && isValidate18(s)) {
                count++;
                matched.add(s);
            }
        }
        int nonRepetitive = matched.size();
        return nonRepetitive >= 100 || count > 0.5 * values.size();
    }

    private static String convert15to18(String idcard) {
        if (isDigital(idcard)) {
            Calendar cday = Calendar.getInstance();
            try {
                cday.setTime(new SimpleDateFormat("yyMMdd").parse(idcard.substring(6, 12)));
            } catch (ParseException e) {
                return null;
            }
            String idcard17 = idcard.substring(0, 6) + String.valueOf(cday.get(Calendar.YEAR)) + idcard.substring(8);
            return idcard17 + VERIFYCODE.get(getPowerSum(converCharToInt(idcard17.toCharArray())) % 11);
        }
        return null;
    }

    private static boolean isValidate18(String idcard) {
        if (!PROVINCES.contains(idcard.substring(0, 2))) {
            return false;
        }
        String idcard17 = idcard.substring(0, 17);
        String idcard18Code = idcard.substring(17, 18);
        if (!isDigital(idcard17)) {
            return false;
        }
        char[] c = idcard17.toCharArray();
        int[] bit = converCharToInt(c);
        int sum17 = getPowerSum(bit);
        String checkCode = VERIFYCODE.get(sum17 % 11);
        return idcard18Code.equalsIgnoreCase(checkCode);
    }

    private static boolean isDigital(String str) {
        return !(str == null || "".equals(str)) && str.matches("^[0-9]*$");
    }

    private static int[] converCharToInt(char[] c) {
        int[] i = new int[c.length];
        int k = 0;
        for (char temp : c) {
            i[k++] = Integer.parseInt(String.valueOf(temp));
        }
        return i;
    }

    private static int getPowerSum(int[] bit) {
        int sum = 0;
        if (POWER.size() != bit.length) {
            return sum;
        }
        for (int i = 0; i < bit.length; i++) {
            for (int j = 0; j < POWER.size(); j++) {
                if (i == j) {
                    sum = sum + bit[i] * POWER.get(j);
                }
            }
        }
        return sum;
    }

    public List<String> IDone(List<String> values, String mask) {
        List<String> result = new ArrayList<>();
        for (String value : values) {
            if (StringUtils.isBlank(value)) {
                result.add(value);
                continue;
            }
            int length = value.length();
            if (length == 15) {
                value = value.substring(0, 6) + SysUtils.buildMask(mask, 9);
            } else if (length == 18) {
                value = value.substring(0, 6) + SysUtils.buildMask(mask, 12);
            }
            result.add(value);
        }
        return result;
    }

    public List<String> IDtwo(List<String> values, String mask) {
        List<String> result = new ArrayList<>();
        for (String value : values) {
            if (StringUtils.isBlank(value)) {
                result.add(value);
                continue;
            }
            int length = value.length();
            if (length == 15) {
                value = SysUtils.buildMask(mask, 6) + value.substring(6, 12) + SysUtils.buildMask(mask, 3);
            } else if (length == 18) {
                value = SysUtils.buildMask(mask, 6) + value.substring(6, 14) + SysUtils.buildMask(mask, 4);
            }
            result.add(value);
        }
        return result;
    }

    public List<String> IDthree(List<String> values, String mask) {
        List<String> result = new ArrayList<>();
        for (String value : values) {
            if (StringUtils.isBlank(value)) {
                result.add(value);
                continue;
            }
            int length = value.length();
            if (length == 15) {
                value = value.substring(0, 12) + SysUtils.buildMask(mask, 3);
            } else if (length == 18) {
                value = value.substring(0, 14) + SysUtils.buildMask(mask, 4);
            }
            result.add(value);
        }
        return result;
    }

    public List<String> IDfour(List<String> values) {
        List<String> result = new ArrayList<>();
        for (String value : values) {
            if (StringUtils.isBlank(value)) {
                result.add(value);
                continue;
            }
            int length = value.length();
            if (length == 15) {
                value = RandomStringUtils.random(15, false, true);
            } else if (length == 18) {
                value = RandomStringUtils.random(18, false, true);
            }
            result.add(value);
        }
        return result;
    }
}