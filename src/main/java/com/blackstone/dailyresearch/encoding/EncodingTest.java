package com.blackstone.dailyresearch.encoding;

import com.blackstone.dailyresearch.util.HexUtils;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;


public class EncodingTest {
    private static String dir = "D:/encoding_test/";
    private static Map<String, String> bomMap = new HashMap<String, String>();

    static {
        File dirFile = new File(dir);
        try {
            FileUtils.deleteDirectory(dirFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        dirFile.mkdirs();
    }

    static {
        bomMap.put("FEFF", "UTF-16BE");
        bomMap.put("0000FEFF", "UTF-32BE");
        bomMap.put("FFFE", "UTF-16LE");
        bomMap.put("0000FFFE", "UTF-32LE");
        bomMap.put("EFBBBF", "UTF-8");
    }


    private static void testFile(String content, String encoding, byte[] bom) throws IOException {
        String withBom = bom != null && bom.length > 0 ? "_withBom" : "";

        File goal = new File(dir + "file_encoding_" + encoding.replace("-", "") + withBom + ".txt");
        write(goal, content, encoding, bom);
        System.out.println("文件所在位置：" + goal.getAbsolutePath());
        System.out.println("文件写入编码：" + encoding);
        System.out.println("文件探测编码：" + EncodeDetector.getEncoding(goal));
        byte[] contentBytes = FileUtils.readFileToByteArray(goal);
        String newEncoding = EncodeDetector.getEncoding(contentBytes);
        System.out.println("字节探测编码：" + newEncoding);
        System.out.println("文件内容(hex)：" + HexUtils.toHex(contentBytes));
        System.out.println("文件内容(字符)：" + new String(contentBytes, newEncoding));
        System.out.println(StringUtils.leftPad("", 180, "-"));
    }

    private static Object[] getBytesFromHttp(String url) throws IOException {
        HttpClient httpClient = new HttpClient();
        GetMethod getMethod = new GetMethod(url);
        int statusCode = httpClient.executeMethod(getMethod);
        System.out.println("http响应头：\r\n" + Arrays.toString(getMethod.getResponseHeaders()));
        String respCharset = getMethod.getResponseCharSet();
        System.out.println("http探测charset：" + respCharset);
        System.out.println(StringUtils.leftPad("", 180, "-"));

        if (statusCode == HttpStatus.SC_OK) {
            Long retLen = getMethod.getResponseContentLength();
            System.err.println("getResponseContentLength？:" + retLen);
            byte[] tmpBytes = new byte[100000];
            int actualLen = getMethod.getResponseBodyAsStream().read(tmpBytes);
            if (actualLen != retLen.intValue()) {
                System.err.println("实际读取报文长度" + actualLen + "和getResponseContentLength不一致" + retLen + "，协议问题？");
                //return;
            }
            byte[] resBytes = new byte[actualLen];
            System.arraycopy(tmpBytes, 0, resBytes, 0, actualLen);
            return new Object[]{resBytes, respCharset};
        } else {
            System.err.println("http get resp fail, code:" + statusCode);
            return null;
        }
    }

    private static void testHttp(String url, String prefix) throws IOException {
        System.out.println(StringUtils.leftPad("", 180, "-"));
        //第一步:获取字节流
        Object[] ret = getBytesFromHttp(url);
        byte[] resBytes = (byte[]) ret[0];
        String respCharset = (String) ret[1];

        String thePrefix = "http_" + prefix + "_";

        File goalFile = new File(dir + thePrefix + "encoding_ori.txt");
        write(goalFile, resBytes);
        System.out.println("原始流编码为(byte)：" + EncodeDetector.getEncoding(resBytes));
        System.out.println("原始流编码为(file)：" + EncodeDetector.getEncoding(goalFile));

        //使用自动获取编码
        if (respCharset != null) {
            System.out.println("http响应中包含字符集：" + respCharset);
            httpWrite(resBytes, respCharset, respCharset, thePrefix + "encoding_auto_");
            httpWrite(resBytes, respCharset, "UTF-8", thePrefix + "encoding_auto_");
            httpWrite(resBytes, respCharset, "GBK", thePrefix + "encoding_auto_");
        }

        //猜编码，硬编码
        //用UTF-8进行原始解码
        httpWrite(resBytes, "UTF-8", "UTF-8", thePrefix + "encoding_force_");
        httpWrite(resBytes, "UTF-8", "GBK", thePrefix + "encoding_force_");

        //用GBK进行原始解码
        httpWrite(resBytes, "GBK", "UTF-8", thePrefix + "encoding_force_");
        httpWrite(resBytes, "GBK", "GBK", thePrefix + "encoding_force_");

        //用ISO-8859-1进行原始解码
        httpWrite(resBytes, "ISO-8859-1", "UTF-8", thePrefix + "encoding_force_");
        httpWrite(resBytes, "ISO-8859-1", "GBK", thePrefix + "encoding_force_");

        //iso-x是单字节解码
        httpWrite(resBytes, "ISO-8859-1", "ISO-8859-1", thePrefix + "encoding_force_");
    }

    private static void httpWrite(byte[] respBytes, String readEncoding, String writeEncoding, String thePrefix) throws IOException {
        System.out.println(StringUtils.leftPad("", 180, "-"));
        String readConvertStr = new String(respBytes, readEncoding);
        System.out.println(readConvertStr);
        File goalFile = new File(dir + thePrefix
                + readEncoding.replace("-", "")
                + "_"
                + writeEncoding.replace("-", "")
                + ".txt");
        write(goalFile, readConvertStr, writeEncoding, null);
        System.out.println(goalFile.getName() + ":读转换编码：" + readEncoding + "，写入文件编码：" + writeEncoding + ",文件实际编码：" + EncodeDetector.getEncoding(goalFile));
    }

    private static void write(File f, String str, String encoding, byte[] bom) throws IOException {
        BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(f));
        if (bom != null && bom.length > 0) {
            os.write(bom);
        }
        os.write(str.getBytes(encoding));
        os.close();
    }

    private static void write(File f, byte[] bytes) throws IOException {
        BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(f));
        os.write(bytes);
        os.close();
    }
    private static void hide() throws UnsupportedEncodingException {
        String content = "众禄基金";
        String gbkHex = HexUtils.toHex(content.getBytes("GBK"));
        String utf8Hex = HexUtils.toHex(content.getBytes("utf-8"));
        System.out.println(gbkHex);
        System.out.println(utf8Hex);

        System.out.println(new String(HexUtils.toBytes(gbkHex),"GBK"));
        System.out.println(new String(HexUtils.toBytes(utf8Hex),"utf-8"));
    }

    private static void testFileRW() throws IOException {
        String content = "a解a放区的天，是晴朗的天a";

        testFile(content, "GB2312", null);
        testFile(content, "GBK", null);
        testFile(content, "GB18030", null);

        //汉字写入ISO-8859-1，必然导致乱码。
        testFile(content, "ISO-8859-1", null);
        //繁体中文如果完全包含简体中文，这里也是有问题的。这里的编码探测也是有问题的！
        testFile(content, "BIG5", null);

        testFile(content, "UTF-8", null);
        //写入bom进行测试
        for (Map.Entry<String, String> entry : bomMap.entrySet()) {
            testFile(content, entry.getValue(), HexUtils.toBytes(entry.getKey()));
        }
    }

    private static void testFirst() throws IOException {
        String hex1 = "D6DAC2BBBBF9BDF0";
        String hex2 = "E4BC97E7A684E59FBAE98791";
        write(new File("D:/encoding_test/hex1"), HexUtils.toBytes(hex1));
        write(new File("D:/encoding_test/hex2"), HexUtils.toBytes(hex2));
    }

    public static void main(String[] args) throws IOException {
        //testFirst();
         testFileRW();

         //testHttp("http://pv.sohu.com/cityjson?ie=GBK", "sohu");

         //testHttp("https://www.99bill.com/seashell/common/errorvpos.html", "99bill");
    }

}
