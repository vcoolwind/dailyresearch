package com.balckstone.dailyresearch.crypto;


import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES对称加密
 * Created by gc on 2017/1/4.
 */
public class AESUtils {

    private AESUtils() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /*
     * 生成密钥
     */
    public static byte[] initKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);  //192 256
        SecretKey secretKey = keyGen.generateKey();
        return secretKey.getEncoded();
    }

    /*
     * AES 加密
     */
    public static byte[] encrypt(byte[] data, byte[] key) throws Exception {
        SecretKey secretKey = new SecretKeySpec(key, "AES");

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] cipherBytes = cipher.doFinal(data);
        return cipherBytes;
    }

    /*
     * AES 解密
     */
    public static byte[] decrypt(byte[] data, byte[] key) throws Exception {
        SecretKey secretKey = new SecretKeySpec(key, "AES");

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] plainBytes = cipher.doFinal(data);
        return plainBytes;
    }

}
