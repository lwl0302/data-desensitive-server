package com.mrray.datadesensitiveserver.algorithm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.List;

public interface BaseAlgorithm {
    //匹配是否是敏感信息
    boolean match(List<String> values);

    Logger logger = LoggerFactory.getLogger("BaseAlgorithm");

    //传入脱敏方法名反射调用脱敏
    default List<String> desensitive(List<String> values, String methodName, Object... objects) {
        List<String> result;
        int length = objects.length;
        Class<?>[] classes = new Class[length + 1];
        Object[] args = new Object[length + 1];
        args[0] = values;
        classes[0] = List.class;
        for (int i = 1; i <= length; i++) {
            Object object = objects[i - 1];
            if (object instanceof String) {
                classes[i] = String.class;
                args[i] = object.toString();
            } else if (object instanceof BigDecimal) {
                classes[i] = double.class;
                args[i] = ((BigDecimal) object).doubleValue();
            } else if (object instanceof Integer) {
                classes[i] = double.class;
                args[i] = object;
            }
        }
        try {
            Method method = this.getClass().getMethod(methodName, classes);
            result = (List<String>) method.invoke(this, args);
            //result.forEach(System.out::println);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return values;
        }
        return result;
    }

    /*default List<String> DES(List<String> values) {
        List<String> result = new ArrayList<>();
        for (String value : values) {
            try {
                result.add(SysUtils.encryptBASE64(EncrypDES.Encrytor(value)));
            } catch (Exception e) {
                result.add("");
            }
        }
        return result;
    }

    default List<String> DES3(List<String> values) {
        List<String> result = new ArrayList<>();
        for (String value : values) {
            try {
                result.add(SysUtils.encryptBASE64(Encryp3DES.Encrytor(value)));
            } catch (Exception e) {
                result.add("");
            }
        }
        return result;
    }

    default List<String> AES(List<String> values) {
        List<String> result = new ArrayList<>();
        for (String value : values) {
            try {
                result.add(SysUtils.encryptBASE64(EncrypAES.Encrytor(value)));
            } catch (Exception e) {
                result.add("");
            }
        }
        return result;
    }

    default List<String> SHA(List<String> values) {
        List<String> result = new ArrayList<>();
        for (String value : values) {
            try {
                result.add(SysUtils.encryptBASE64(EncrypSHA.eccrypt(value)));
            } catch (Exception e) {
                result.add("");
            }
        }
        return result;
    }

    default List<String> MD5(List<String> values) {
        List<String> result = new ArrayList<>();
        for (String value : values) {
            try {
                result.add(SysUtils.encryptBASE64(EncrypMD5.eccrypt(value)));
            } catch (Exception e) {
                result.add("");
            }
        }
        return result;
    }*/
}