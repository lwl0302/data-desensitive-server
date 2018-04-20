package com.mrray.datadesensitiveserver.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES 加密
 */
public class AESEncryption {
    /*public static byte[] encrypt(String serial, byte[] data) {
        try {
            IvParameterSpec iv = new IvParameterSpec("1568942365795462".getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(serial.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            return cipher.doFinal(data);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }*/

    public static byte[] decrypt(String serial, byte[] data) throws Exception {
        IvParameterSpec iv = new IvParameterSpec("1568942365795462".getBytes("UTF-8"));
        SecretKeySpec skeySpec = new SecretKeySpec(serial.getBytes("UTF-8"), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
        return cipher.doFinal(data);
    }
}
