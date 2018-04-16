package com.balckstone.dailyresearch.crypto;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class RSAEncryptUtil {
    protected static final String ALGORITHM = "RSA";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static String encrypt(String text, String keyPath) throws Exception {
        String publicKeyStr = FileUtils.readFileToString(new File(keyPath));

        byte[] encBytes = encrypt(text.getBytes("utf-8"), getPublicKeyFromString(publicKeyStr));
        return Base64.encodeBase64String(encBytes);
    }

    public static String decrypt(String text, String keyPath) throws Exception {
        String privateKeyStr = FileUtils.readFileToString(new File(keyPath));
        byte[] encBytes = Base64.decodeBase64(text.getBytes());

        byte[] decBytes = decrypt(encBytes, getPrivateKeyFromString(privateKeyStr));
        return new String(decBytes, "utf-8");
    }


    /**
     * Encrypt a text using public key.
     *
     * @param text The original unencrypted text
     * @param key  The public key
     * @return Encrypted text
     * @throws Exception
     */
    public static byte[] encrypt(byte[] text, Key key) throws Exception {
        byte[] cipherText = null;
        //
        // get an RSA cipher object and print the provider
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

        // encrypt the plaintext using the public key
        cipher.init(Cipher.ENCRYPT_MODE, key);
        cipherText = cipher.doFinal(text);

        return cipherText;
    }


    /**
     * Decrypt text using private key
     *
     * @param text The encrypted text
     * @param key  The private key
     * @return The unencrypted text
     * @throws Exception
     */
    public static byte[] decrypt(byte[] text, PrivateKey key) throws Exception {
        byte[] dectyptedText = null;
        // decrypt the text using the private key
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        dectyptedText = cipher.doFinal(text);
        return dectyptedText;

    }


    /**
     * Generates Private Key from BASE64 encoded string
     *
     * @param key BASE64 encoded string which represents the key
     * @return The PrivateKey
     * @throws Exception
     */
    public static PrivateKey getPrivateKeyFromString(String key) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(key));
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
        return privateKey;
    }

    /**
     * Generates Public Key from BASE64 encoded string
     *
     * @param key BASE64 encoded string which represents the key
     * @return The PublicKey
     * @throws Exception
     */
    public static PublicKey getPublicKeyFromString(String key) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64.decodeBase64(key));
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
        return publicKey;
    }


    /**
     * Encrypt file using 1024 RSA encryption
     *
     * @param srcFileName  Source file name
     * @param destFileName Destination file name
     * @param key          The key. For encryption this is the Private Key and for decryption this is the public key
     * @throws Exception
     */
    public static void encryptFile(String srcFileName, String destFileName, PublicKey key) throws Exception {
        encryptDecryptFile(srcFileName, destFileName, key, Cipher.ENCRYPT_MODE);
    }

    /**
     * Decrypt file using 1024 RSA encryption
     *
     * @param srcFileName  Source file name
     * @param destFileName Destination file name
     * @param key          The key. For encryption this is the Private Key and for decryption this is the public key
     * @throws Exception
     */
    public static void decryptFile(String srcFileName, String destFileName, PrivateKey key) throws Exception {
        encryptDecryptFile(srcFileName, destFileName, key, Cipher.DECRYPT_MODE);
    }

    /**
     * Encrypt and Decrypt files using 1024 RSA encryption
     *
     * @param srcFileName  Source file name
     * @param destFileName Destination file name
     * @param key          The key. For encryption this is the Private Key and for decryption this is the public key
     * @param cipherMode   Cipher Mode
     * @throws Exception
     */
    public static void encryptDecryptFile(String srcFileName, String destFileName, Key key, int cipherMode) throws Exception {
        OutputStream outputWriter = null;
        InputStream inputReader = null;
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            String textLine = null;
            //RSA encryption data size limitations are slightly less than the key modulus size,
            //depending on the actual padding scheme used (e.g. with 1024 bit (128 byte) RSA key,
            //the size limit is 117 bytes for PKCS#1 v 1.5 padding. (http://www.jensign.com/JavaScience/dotnet/RSAEncrypt/)
            byte[] buf = cipherMode == Cipher.ENCRYPT_MODE ? new byte[100] : new byte[128];
            int bufl;
            // init the Cipher object for Encryption...
            cipher.init(cipherMode, key);

            // start FileIO
            outputWriter = new FileOutputStream(destFileName);
            inputReader = new FileInputStream(srcFileName);
            while ((bufl = inputReader.read(buf)) != -1) {
                byte[] encText = null;
                if (cipherMode == Cipher.ENCRYPT_MODE) {
                    encText = encrypt(copyBytes(buf, bufl), (PublicKey) key);
                } else {
                    encText = decrypt(copyBytes(buf, bufl), (PrivateKey) key);
                }
                outputWriter.write(encText);
            }
            outputWriter.flush();

        } finally {
            try {
                if (outputWriter != null) {
                    outputWriter.close();
                }
                if (inputReader != null) {
                    inputReader.close();
                }
            } catch (Exception e) {
                // do nothing...
            } // end of inner try, catch (Exception)...
        }
    }

    public static byte[] copyBytes(byte[] arr, int length) {
        byte[] newArr = null;
        if (arr.length == length) {
            newArr = arr;
        } else {
            newArr = new byte[length];
            for (int i = 0; i < length; i++) {
                newArr[i] = (byte) arr[i];
            }
        }
        return newArr;
    }

    public static void main(String[] args) throws Exception {
        testWihtPKCS1();
        testWihtPKCS8();
    }

    private static void testWihtPKCS1() throws Exception {
        String pubKey = RSAEncryptUtil.class.getResource("/ssl/rsa_public_key.pem").getPath();
        String priKey = RSAEncryptUtil.class.getResource("/ssl/rsa_private_key.pem").getPath();

        String plian = "abc123";
        System.out.println(plian);

        String encText = RSAEncryptUtil.encrypt(plian, pubKey);
        System.out.println(encText);

        String decText = RSAEncryptUtil.decrypt(encText, priKey);
        System.out.println(decText);
    }

    private static void testWihtPKCS8() throws Exception {
        String pubKey = RSAEncryptUtil.class.getResource("/ssl/rsa_public_key.pem").getPath();
        String priKey = RSAEncryptUtil.class.getResource("/ssl/pkcs8_rsa_private_key.pem").getPath();

        String plian = "abc123";
        System.out.println(plian);

        String encText = RSAEncryptUtil.encrypt(plian, pubKey);
        System.out.println(encText);

        String decText = RSAEncryptUtil.decrypt(encText, priKey);
        System.out.println(decText);
    }

}