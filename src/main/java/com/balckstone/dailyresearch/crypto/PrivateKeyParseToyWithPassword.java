package com.balckstone.dailyresearch.crypto;

import java.io.File;
import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.EncryptedPrivateKeyInfo;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.apache.commons.io.FileUtils;


public class PrivateKeyParseToyWithPassword {
    public static void main(String[] args) throws Exception {
        String priKey = PrivateKeyParseToyWithPassword.class.getResource("/pkcs8_private_key.pem").getPath();
        String passwd = "123456";

        byte[] keyBytes = FileUtils.readFileToByteArray(new File(priKey)) ;


        EncryptedPrivateKeyInfo encryptPKInfo = new EncryptedPrivateKeyInfo(keyBytes);

        Cipher cipher = Cipher.getInstance(encryptPKInfo.getAlgName());
        PBEKeySpec pbeKeySpec = new PBEKeySpec(passwd.toCharArray());
        SecretKeyFactory secFac = SecretKeyFactory.getInstance(encryptPKInfo.getAlgName());
        Key pbeKey = secFac.generateSecret(pbeKeySpec);
        AlgorithmParameters algParams = encryptPKInfo.getAlgParameters();
        cipher.init(Cipher.DECRYPT_MODE, pbeKey, algParams);
        KeySpec pkcs8KeySpec = encryptPKInfo.getKeySpec(cipher);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        RSAPrivateKey privKey = (RSAPrivateKey) kf.generatePrivate(pkcs8KeySpec);
        System.out.println(privKey.getPrivateExponent());


    }
}