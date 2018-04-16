package com.balckstone.dailyresearch.crypto;

import java.io.File;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.RSAPrivateKeySpec;

import org.apache.commons.io.FileUtils;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.pkcs.RSAPrivateKeyStructure;

public class PrivateKeyParseToy {
    public static void main(String[] args) throws Exception {
        String b64encoded = "MIIBOwIBAAJBALf+EfAvqNlTrTwPy1BrqpXkX21KUu2My/JVupfDHIQ84e+uNUYm"
                + "2UrcPVYjg8+C9M5dZU83s8jvu/cwOw14MyECAwEAAQJALTVhZPng7B1yWGqtE0KR"
                + "NKlbhTgY7kOFLTNBWN7ZF+iPENnJLIjze8zfkMsQsTaD2Sa8NCigBT1ClZDBHNLT"
                + "QQIhAN4Jt6r6Hf81MRSaHKVogkUkIKsyMQ3jzxT9xKF0SkPZAiEA1CKdbva93MJo"
                + "KRr9SmgYqKziAmLFj2bpXOz/tSvuhIkCIQCa4aZnsq7H/b+twk6nJv5v4mKTaKCF"
                + "MtqZpubJRMglCQIgNmEpOmjGAvFTAjaI96n3qEWpKjNnsXsQF2Ipqqe4XQECIQCO"
                + "19jR6lk+DIrjKl76tBHsuH5fIoJhvE0Nk1OdwLlLPA==";
        byte[] asn1PrivateKeyBytes = org.apache.commons.codec.binary.Base64.decodeBase64(b64encoded.getBytes("US-ASCII"));
        RSAPrivateKeyStructure asn1PrivKey = new RSAPrivateKeyStructure((ASN1Sequence) ASN1Sequence.fromByteArray(asn1PrivateKeyBytes));
        RSAPrivateKeySpec rsaPrivKeySpec = new RSAPrivateKeySpec(asn1PrivKey.getModulus(), asn1PrivKey.getPrivateExponent());
        KeyFactory kf = KeyFactory.getInstance("RSA");
        RSAPrivateKey privKey = (RSAPrivateKey) kf.generatePrivate(rsaPrivKeySpec);
        System.out.println(privKey.getPrivateExponent());

        test2();
    }

    public static void test2() throws Exception {

        String priKey = PrivateKeyParseToy.class.getResource("/ssl/rsa_private_key.pem").getPath();
        String passwd = "123456";

        byte[] priKeyData = FileUtils.readFileToByteArray(new File(priKey));

        byte[] asn1PrivateKeyBytes = org.apache.commons.codec.binary.Base64.decodeBase64(priKeyData);
        RSAPrivateKeyStructure asn1PrivKey = new RSAPrivateKeyStructure((ASN1Sequence) ASN1Sequence.fromByteArray(asn1PrivateKeyBytes));
        RSAPrivateKeySpec rsaPrivKeySpec = new RSAPrivateKeySpec(asn1PrivKey.getModulus(), asn1PrivKey.getPrivateExponent());
        KeyFactory kf = KeyFactory.getInstance("RSA");
        RSAPrivateKey privKey = (RSAPrivateKey) kf.generatePrivate(rsaPrivKeySpec);
        System.out.println(privKey.getPrivateExponent());

    }
}