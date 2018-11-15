package com.mrray.datadesensitiveserver.algorithm;

import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DateAlgorithm implements BaseAlgorithm {
    private static final List<String> SIGNS = Arrays.asList("时", "秒", "分", "h", "m", "s", "H", "M", "S", ":");

    @Override
    public boolean match(List<String> values) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int count = 0;
        Set<String> matched = new HashSet<>();
        for (String s : values) {
            if (StringUtils.isBlank(s)) {
                continue;
            }
            int length = s.length();
            if (length == 10 && s.matches("^[0-9]*$")) {
                s = simpleDateFormat.format(Long.valueOf(s + "000"));
                if (checkDate(s)) {
                    count++;
                    matched.add(s);
                }
            } else if (length == 13 && s.matches("^[0-9]*$")) {
                s = simpleDateFormat.format(Long.valueOf(s));
                if (checkDate(s)) {
                    count++;
                    matched.add(s);
                }
            } else if (length >= 6 && length < 24 && checkDate(s)) {
                count++;
                matched.add(s);
            }
        }
        int nonRepetitive = matched.size();
        return nonRepetitive >= 100 || count > 0.5 * values.size();
    }

    private static boolean checkDate(String s) {
        String match = "^[0-9]*$";
        String value = s + " ";
        List<String> numvetor = new ArrayList<>();
        List<String> numlist = new ArrayList<>();
        List<Integer> dayandmonth = new ArrayList<>();
        boolean year = false;
        int day;
        int month;
        for (int i = 0; i < value.length(); i++) {
            String substring = value.substring(i, i + 1);
            if (substring.matches(match) || SIGNS.contains(substring)) {
                numvetor.add(substring);
            } else {
                String numstr = String.join("", numvetor);
                numvetor.clear();
                numlist.add(numstr);
            }
        }
        for (String num : numlist) {
            if (num.matches(match)) {
                Integer integer;
                try {
                    integer = Integer.valueOf(num);
                } catch (Exception e) {
                    return false;
                }
                if (integer >= 1970 && integer <= 2070) {
                    year = true;
                } else if (integer <= 31) {
                    dayandmonth.add(integer);
                }
            } else if (num.contains(":")) {
                String[] split = num.split(":");
                if (split.length == 3 && (Integer.valueOf(split[0]) < 25) && (Integer.valueOf(split[1]) < 61) && (Integer.valueOf(split[2]) < 61)) {
                    return true;
                }
            }
        }
        if (dayandmonth.size() == 2) {
            if (dayandmonth.get(0) >= dayandmonth.get(1)) {
                month = dayandmonth.get(1);
                day = dayandmonth.get(0);
            } else {
                month = dayandmonth.get(0);
                day = dayandmonth.get(1);
            }
            if (year && month <= 12 && day <= 31) {
                return true;
            }
        } else if (dayandmonth.size() == 1) {
            month = dayandmonth.get(0);
            if (year && month <= 12) {
                return true;
            }
        }
        return false;
    }

    public List<String> dateone(List<String> values) {
        List<String> result = new ArrayList<>();
        for (String value : values) {
            if (StringUtils.isBlank(value)) {
                result.add(value);
                continue;
            }
            int length = value.length();
            if ((length == 10 || length == 13) && value.matches("^[0-9]*$")) {
                value = "0";
            } else if (length >= 5 && length <= 12) {
                if (value.contains(":")) {
                    value = "00:00:00";
                } else {
                    value = "2018-01-01";
                }
            } else {
                value = "2018-01-01 00:00:00";
            }
            result.add(value);
        }
        return result;
    }

    public List<String> datetwo(List<String> values, String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long time;
        try {
            time = simpleDateFormat.parse(date).getTime();
        } catch (ParseException e) {
            Date now = new Date();
            date = simpleDateFormat.format(now);
            time = now.getTime();
        }
        List<String> result = new ArrayList<>();
        for (String value : values) {
            if (StringUtils.isBlank(value)) {
                result.add(value);
                continue;
            }
            int length = value.length();
            if ((length == 10 || length == 13) && value.matches("^[0-9]*$")) {
                value = Long.valueOf(time).toString();
                if (length == 10) {
                    value = value.substring(0, 10);
                }
            } else if (length >= 5 && length <= 12) {
                if (value.contains(":")) {
                    value = date.substring(11);
                } else {
                    value = date.substring(0, 10);
                }
            } else {
                value = date;
            }
            result.add(value);
        }
        return result;
    }

    /*public List<String> datetwo(List<String> values) {
        List<String> result = new ArrayList<>();
        for (String value : values) {
            if (StringUtils.isBlank(value)) {
                result.add(value);
                continue;
            }
            int length = value.length();
            if ((length == 10 || length == 13) && value.matches("^[0-9]*$")) {
                value = "0";
            } else {
                String[] split = value.split("");
                StringBuilder stringBuilder = new StringBuilder();
                for (String s : split) {
                    if (s.matches("^[0-9]*$")) {
                        stringBuilder.append("1");
                    } else {
                        stringBuilder.append(s);
                    }
                }
                value = stringBuilder.toString();
            }
            result.add(value);
        }
        return result;
    }*/
}