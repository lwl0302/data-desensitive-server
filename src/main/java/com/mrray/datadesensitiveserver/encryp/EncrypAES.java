package com.mrray.datadesensitiveserver.encryp;

import com.mrray.datadesensitiveserver.utils.YamlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;

public class EncrypAES {

    private static Cipher c;
    private static SecretKey deskey;
    private static Logger logger = LoggerFactory.getLogger("EncrypAES");

    static {
        Security.addProvider(new com.sun.crypto.provider.SunJCE());
        try {
            String key = YamlReader.getKey("AES");
            int length = key.length();
            if (length != 16) {
                key = "4687575872615462";
            }
            deskey = new SecretKeySpec(key.getBytes(), "AES");
            c = Cipher.getInstance("AES");
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * 对字符串加密
     */
    public static byte[] Encrytor(String str) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeySpecException {
        c.init(Cipher.ENCRYPT_MODE, deskey);
        byte[] src = str.getBytes();
        return c.doFinal(src);
    }

    /**
     * 对字符串解密
     */
    public static byte[] Decryptor(byte[] buff) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException {
        c.init(Cipher.DECRYPT_MODE, deskey);
        return c.doFinal(buff);
    }
}
