package com.mrray.datadesensitiveserver.encryp;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncrypMD5 {
    public static byte[] eccrypt(String info) throws NoSuchAlgorithmException {
        //根据MD5算法生成MessageDigest对象
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] srcBytes = info.getBytes();
        //使用srcBytes更新摘要
        md5.update(srcBytes);
        //完成哈希计算，得到result
        return md5.digest();
    }
}
