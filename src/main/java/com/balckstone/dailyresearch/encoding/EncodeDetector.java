package com.balckstone.dailyresearch.encoding;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;
import org.apache.commons.lang.StringUtils;

/**
 * desc:编码检测器
 *
 * @author 王彦锋
 * @date 2018/6/12 16:27
 */
public class EncodeDetector {

    /**
     * 获取字节组编码
     *
     * @param data
     * @return
     */
    public static String getEncoding(byte[] data) {
        byte[] bomHead = new byte[4];
        for (int i = 0; i < bomHead.length && i < data.length; i++) {
            bomHead[i] = data[i];
        }
        String encoding = getEncodingWithBom(bomHead);
        if (encoding == null) {
            CharsetDetector detector = new CharsetDetector();
            detector.setText(data);
            CharsetMatch match = detector.detect();
            encoding = match.getName();
            return encoding;
        }
        return encoding;
    }

    /**
     * 获取输入流编码
     *
     * @param is
     * @return
     * @throws IOException
     */
    public static String getEncoding(BufferedInputStream is) throws IOException {
        if (is == null) {
            return null;
        }
        byte[] bomHead = new byte[4];
        is.mark(4);
        is.read(bomHead,0,bomHead.length);
        is.reset();
        String encoding = getEncodingWithBom(bomHead);
        if (encoding == null) {
            CharsetDetector detector = new CharsetDetector();
            detector.setText(is);
            CharsetMatch match = detector.detect();
            encoding = match.getName();
        }
        return encoding;
    }

    /**
     * 获取文件编码
     *
     * @param f
     * @return
     * @throws IOException
     */
    public static String getEncoding(File f) throws IOException {
        BufferedInputStream is = new BufferedInputStream(new FileInputStream(f));
        return getEncoding(is);
    }


    private static Map<String, String> bomMap = new HashMap<String, String>();

    static {
        bomMap.put("FEFF", "UTF-16BE");
        bomMap.put("0000FEFF", "UTF-32BE");
        bomMap.put("FFFE", "UTF-16LE");
        bomMap.put("0000FFFE", "UTF-32LE");
        bomMap.put("EFBBBF", "UTF-8");
    }

    public static String getEncodingWithBom(byte[] bomHead) {
        if (bomHead != null && bomHead.length >= 2) {
            String hex = toHex(bomHead);
            String twoBytes = StringUtils.substring(hex, 0, 4);
            String threeBytes = StringUtils.substring(hex, 0, 6);
            String fourBytes = StringUtils.substring(hex, 0, 8);
            if (bomMap.containsKey(twoBytes)) {
                System.out.println(">>>>has bom");
                return bomMap.get(twoBytes);
            }
            if (bomMap.containsKey(threeBytes)) {
                System.out.println(">>>>has bom");
                return bomMap.get(threeBytes);
            }
            if (bomMap.containsKey(fourBytes)) {
                System.out.println(">>>>has bom");
                return bomMap.get(fourBytes);
            }

            return null;
        } else {
            return null;
        }
    }


    public static String toHex(byte[] resultBytes) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < resultBytes.length; i++) {
            if (Integer.toHexString(0xFF & resultBytes[i]).length() == 1) {
                builder.append("0").append(
                        Integer.toHexString(0xFF & resultBytes[i]));
            } else {
                builder.append(Integer.toHexString(0xFF & resultBytes[i]));
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
    public static byte[] hexStringToByte(String hex) {
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] achar = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
        }
        return result;
    }

    private static int toByte(char c) {
        byte b = (byte) "0123456789ABCDEF".indexOf(c);
        return b;
    }


}
