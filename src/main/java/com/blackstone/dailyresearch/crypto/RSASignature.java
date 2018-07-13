package com.blackstone.dailyresearch.crypto;

import java.io.File;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.X509EncodedKeySpec;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.pkcs.RSAPrivateKeyStructure;

public class RSASignature {
    /**
     * 签名算法
     */
    public static final String SIGN_ALGORITHMS = "SHA1WithRSA";

    /**
     * RSA签名
     *
     * @param content    待签名数据
     * @param privateKey 商户私钥
     * @param encode     字符集编码
     * @return 签名值
     */
    public static String sign(String content, String privateKey, String encode) {
        try {
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(decodeBASE64(privateKey));

            KeyFactory keyf = KeyFactory.getInstance("RSA");
            PrivateKey priKey = keyf.generatePrivate(priPKCS8);

            java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);

            signature.initSign(priKey);
            signature.update(content.getBytes(encode));

            byte[] signed = signature.sign();

            return encodeBASE64(signed);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String signWithPKCS1(String content, String privateKey, String encode) {
        try {

            byte[] priKeyData = decodeBASE64(privateKey);

            RSAPrivateKeyStructure asn1PrivKey = new RSAPrivateKeyStructure((ASN1Sequence) ASN1Sequence.fromByteArray(priKeyData));
            RSAPrivateKeySpec rsaPrivKeySpec = new RSAPrivateKeySpec(asn1PrivKey.getModulus(), asn1PrivKey.getPrivateExponent());
            KeyFactory kf = KeyFactory.getInstance("RSA");
            RSAPrivateKey rsaKey = (RSAPrivateKey) kf.generatePrivate(rsaPrivKeySpec);
            System.out.println(rsaKey.getPrivateExponent());

            java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);

            signature.initSign(rsaKey);
            signature.update(content.getBytes(encode));

            byte[] signed = signature.sign();

            return encodeBASE64(signed);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * RSA验签名检查
     *
     * @param content   待签名数据
     * @param sign      签名值
     * @param publicKey 分配给开发商公钥
     * @param encode    字符集编码
     * @return 布尔值
     */
    public static boolean verify(String content, String sign, String publicKey, String encode) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] encodedKey = decodeBASE64(publicKey);
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));

            java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);

            signature.initVerify(pubKey);
            signature.update(content.getBytes(encode));

            return signature.verify(decodeBASE64(sign));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    /**
     * Encode bytes array to BASE64 string
     *
     * @param bytes
     * @return Encoded string
     */
    private static String encodeBASE64(byte[] bytes) {
        return Base64.encodeBase64String(bytes);
    }

    /**
     * Decode BASE64 encoded string to bytes array
     *
     * @param text The string
     * @return Bytes array
     * @throws IOException
     */
    private static byte[] decodeBASE64(String text) throws IOException {
        return Base64.decodeBase64(text);
    }

    public static void main(String[] args) throws IOException {
        testWithPKCS8();
        testWithPKCS1();
    }

    private static void testWithPKCS8() throws IOException {
        String pubKey = RSASignature.class.getResource("/ssl/rsa_public_key.pem").getPath();
        String priKey = RSASignature.class.getResource("/ssl/pkcs8_rsa_private_key.pem").getPath();

        String publicKeyStr = FileUtils.readFileToString(new File(pubKey));
        String privateKeyStr = FileUtils.readFileToString(new File(priKey));

        String content = "abc1223";
        System.out.println(content);

        String sign = RSASignature.sign(content, privateKeyStr, "utf-8");
        System.out.println(sign);

        boolean flag = RSASignature.verify(content, sign, publicKeyStr, "utf-8");
        System.out.println(flag);
    }


    private static void testWithPKCS1() throws IOException {
        String pubKey = RSASignature.class.getResource("/ssl/rsa_public_key.pem").getPath();
        String priKey = RSASignature.class.getResource("/ssl/rsa_private_key.pem").getPath();

        String publicKeyStr = FileUtils.readFileToString(new File(pubKey));
        String privateKeyStr = FileUtils.readFileToString(new File(priKey));

        String content = "abc1223";
        System.out.println(content);

        String sign = RSASignature.signWithPKCS1(content, privateKeyStr, "utf-8");
        System.out.println(sign);

        boolean flag = RSASignature.verify(content, sign, publicKeyStr, "utf-8");
        System.out.println(flag);
    }

}
