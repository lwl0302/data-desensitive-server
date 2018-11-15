package com.mrray.datadesensitiveserver.algorithm;

import com.mrray.datadesensitiveserver.utils.SysUtils;
import org.apache.commons.lang.StringUtils;

import java.util.*;

public class NameAlgorithm implements BaseAlgorithm {
    private static final List<String> SURNAMES = Arrays.asList(
            "李", "王", "张", "刘", "陈", "杨", "赵", "黄", "周", "吴", "徐", "孙", "胡", "朱", "高",
            "林", "何", "郭", "马", "罗", "梁", "宋", "郑", "谢", "韩", "唐", "冯", "于", "董", "萧",
            "程", "曹", "袁", "邓", "许", "傅", "沈", "曾", "彭", "吕", "苏", "卢", "蒋", "蔡", "贾",
            "丁", "魏", "薛", "叶", "阎", "余", "潘", "杜", "戴", "夏", "钟", "汪", "田", "任", "姜",
            "范", "方", "石", "姚", "谭", "廖", "邹", "熊", "金", "陆", "郝", "孔", "白", "崔", "康",
            "毛", "邱", "秦", "江", "史", "顾", "侯", "邵", "孟", "龙", "万", "段", "漕", "钱", "汤",
            "尹", "黎", "易", "常", "武", "乔", "贺", "赖", "龚", "文", "庞", "樊", "兰", "殷", "施",
            "陶", "洪", "翟", "安", "颜", "倪", "严", "牛", "温", "芦", "季", "俞", "章", "鲁", "葛",
            "伍", "韦", "申", "尤", "毕", "聂", "丛", "焦", "向", "柳", "邢", "路", "岳", "齐", "沿",
            "梅", "莫", "庄", "辛", "管", "祝", "左", "涂", "谷", "祁", "时", "舒", "耿", "牟", "卜",
            "路", "詹", "关", "苗", "凌", "费", "纪", "靳", "盛", "童", "欧", "甄", "项", "曲", "成",
            "游", "阳", "裴", "席", "卫", "查", "屈", "鲍", "位", "覃", "霍", "翁", "隋", "植", "甘",
            "景", "薄", "单", "包", "司", "柏", "宁", "柯", "阮", "桂", "闵", "蔺", "解", "强", "柴",
            "华", "车", "冉", "房", "边", "辜", "吉", "饶", "刁", "瞿", "戚", "丘", "古", "米", "池",
            "滕", "晋", "苑", "邬", "臧", "畅", "宫", "来", "嵺", "苟", "全", "褚", "廉", "简", "娄",
            "盖", "符", "奚", "木", "穆", "党", "燕", "郎", "邸", "冀", "谈", "姬", "屠", "连", "郜",
            "晏", "栾", "郁", "商", "蒙", "计", "喻", "揭", "窦", "迟", "宇", "敖", "糜", "鄢", "冷",
            "卓", "花", "仇", "艾", "蓝", "都", "巩", "稽", "井", "练", "仲", "乐", "虞", "卞", "封",
            "竺", "冼", "原", "官", "衣", "楚", "佟", "栗", "匡", "宗", "应", "台", "巫", "鞠", "僧",
            "桑", "荆", "谌", "银", "扬", "明", "沙", "薄", "伏", "岑", "习", "胥", "保", "和", "蔺"
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
            if (length > 1 && length < 4 && SURNAMES.contains(s.substring(0, 1))) {
                count++;
                matched.add(s);
            }
        }
        int nonRepetitive = matched.size();
        return nonRepetitive >= 100 || count > 0.5 * values.size();
    }

    public List<String> nameone(List<String> values) {
        List<String> result = new ArrayList<>();
        for (String value : values) {
            if (StringUtils.isBlank(value)) {
                result.add(value);
                continue;
            }
            int length = value.length();
            StringBuilder temp = new StringBuilder(value.substring(0, 1));
            for (int i = 0; i < length - 1; i++) {
                temp.append("*");
            }
            result.add(temp.toString());
        }
        return result;
    }

    public List<String> nametwo(List<String> values) {
        List<String> result = new ArrayList<>();
        for (String value : values) {
            if (StringUtils.isBlank(value)) {
                result.add(value);
                continue;
            }
            value = value.substring(0, 1) + SysUtils.buildMask("*", 2);
            result.add(value);
        }
        return result;
    }
}