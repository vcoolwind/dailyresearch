package util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Locale;

public class HexUtils {


    /**
     * 把字节数组转变为16进制字符串展示
     *
     * @param bytes
     * @return string with HEX value of the key
     */
    public static String toHex(byte[] bytes) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        for (byte b : bytes) {
            ps.printf("%x", b);
        }
        return baos.toString().toUpperCase();
    }

    /**
     * 把16进制字符串转换成字节数组
     *
     * @param hexData
     * @return byte[]
     */
    public static byte[] toBytes(String hexData) {
        if ((hexData.length() & 1) != 0  ||
                hexData.replaceAll("[a-fA-F0-9]", "").length() > 0) {
            throw new java.lang.IllegalArgumentException("'" + hexData + "' is not a hex string");
        }

        //byte[] result = new byte[(hexData.length() + 1) / 2];
        byte[] result = new byte[hexData.length()/ 2];
        String hexNumber = null;
        int stringOffset = 0;
        int byteOffset = 0;
        while (stringOffset < hexData.length()) {
            hexNumber = hexData.substring(stringOffset, stringOffset + 2);
            stringOffset += 2;
            result[byteOffset++] = (byte) Integer.parseInt(hexNumber, 16);
        }
        return result;
    }

    /**
     * Converts byte data to a Hex-encoded string.
     *
     * @param data
     * data to hex encode.
     * @return
     * hex-encoded string.
     */
    public static String toHexV2(byte[] data) {
        StringBuilder sb = new StringBuilder(data.length * 2);
        for (int i = 0; i < data.length; i++) {
            String hex = Integer.toHexString(data[i]);
            if (hex.length() == 1) {
                // Append leading zero.
                sb.append("0");
            } else if (hex.length() == 8) {
                // Remove ff prefix from negative numbers.
                hex = hex.substring(6);
            }
            sb.append(hex);
        }
        return sb.toString().toLowerCase(Locale.getDefault());
    }



    @Deprecated
    public static String toHexV1(byte[] data) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            if (Integer.toHexString(0xFF & data[i]).length() == 1) {
                builder.append("0").append(
                        Integer.toHexString(0xFF & data[i]));
            } else {
                builder.append(Integer.toHexString(0xFF & data[i]));
            }
        }
        return builder.toString().toUpperCase();
    }

    /**
     * 把16进制字符串转换成字节数组
     *
     * @param hex
     * @return byte[]
     */
    public static byte[] toBytesV1(String hex) {
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] achar = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
        }
        return result;
    }

    public static int toByte(char c) {
        byte b = (byte) "0123456789ABCDEF".indexOf(c);
        return b;
    }
}
