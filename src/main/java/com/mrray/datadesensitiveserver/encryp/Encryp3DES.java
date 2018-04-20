package com.mrray.datadesensitiveserver.encryp;

import com.mrray.datadesensitiveserver.utils.YamlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.*;
import javax.crypto.spec.DESedeKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;

public class Encryp3DES {
    private static Cipher c;
    private static SecretKey deskey;
    private static Logger logger = LoggerFactory.getLogger("Encryp3DES");
    private static final String TYPE = "DESede";

    static {
        Security.addProvider(new com.sun.crypto.provider.SunJCE());
        try {
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(TYPE);
            String key = YamlReader.getKey(TYPE);
            int length = key.length();
            if (length != 24) {
                key = "468758875487236594125462";
            }
            deskey = keyFactory.generateSecret(new DESedeKeySpec(key.getBytes()));
            c = Cipher.getInstance(TYPE);
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
