package com.balckstone.dailyresearch.crypto;

public class DESUtilsTest {
    public static void main(String[] args){
        String ori="abcdefgh";

        String key="issecret";
        //String key="这是密码";

        try {
            byte[] dest = DESUtils.encrypt(ori.getBytes(),key.getBytes("GBK"));
            System.out.println(dest.length);
            //System.out.println(Base64.encodeBase64String(dest));
            String encHex = toHex(dest);
            System.out.println(encHex);

            byte[] encBinary= toBytes(encHex);

            byte[] plainBytes = DESUtils.decrypt(encBinary,key.getBytes("GBK"));
            System.out.println(new String(plainBytes));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static String toHex(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    private static byte[] toBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    /**
     * Convert char to byte
     *
     * @param c char
     * @return byte
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }
}
