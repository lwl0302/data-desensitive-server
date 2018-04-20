package com.mrray.datadesensitiveserver.encryp;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncrypSHA {
    public static byte[] eccrypt(String info) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("SHA");
        byte[] srcBytes = info.getBytes();
        //使用srcBytes更新摘要
        md5.update(srcBytes);
        //完成哈希计算，得到result
        return md5.digest();
    }
}
