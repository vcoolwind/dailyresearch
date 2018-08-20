package com.blackstone.dailyresearch.util;

import com.blackstone.dailyresearch.util.third.MessageFormatter;
import java.util.Random;
import org.apache.commons.lang.StringUtils;

public class StringHelper {
    private StringHelper(){

    }


    public static String getRandomString(int length) {
        //定义一个字符串（A-Z，a-z，0-9）即62位；
        String str = "zxcvbnmlkjhgfdsaqwertyuiopQWERTYUIOPASDFGHJKLZXCVBNM1234567890";
        //由Random生成随机数
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        //长度为几就循环几次
        for (int i = 0; i < length; ++i) {
            //产生0-61的数字
            int number = random.nextInt(62);
            //将产生的数字通过length次承载到sb中
            sb.append(str.charAt(number));
        }
        //将承载的字符转换成字符串
        return sb.toString();
    }



    /**
     * 根据参数格式化字符串（模式：{}）
     * StringHelper.format("hello {},{}","wyf","wangb")
     *
     * @param format
     * @param paras
     * @return
     * @author 王彦锋
     * @date 2017/12/13 17:29
     */
    public static String format(String format, Object... paras) {
        return MessageFormatter.arrayFormat(format, paras).getMessage();
    }


    /**
     * 参数中是否有空对象(null or "")
     *
     * @param objs
     * @return 只要有一个就返回true
     * @author 王彦锋
     * @date 2017/12/13 17:45
     */
    public static boolean isHasBlank(String... objs) {
        if (objs == null) {
            return true;
        }
        for (String obj : objs) {
            if (isBlank(obj)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNotBlank(String obj) {
        return StringUtils.isNotBlank(obj);
    }

    public static boolean isBlank(String obj) {
        return StringUtils.isBlank(obj);
    }

    /**
     * 参数全部不为空返回True，否则false
     *
     * @param objs
     * @return
     */
    public static boolean isNotHasBlank(String... objs) {
        return !isHasBlank(objs);
    }


    /**
     * 当参数为null或参数内对象全部为空时，返回True，否则返回False。
     *
     * @param objs
     * @return
     * @author 王彦锋
     * @date 2017/12/13 17:48
     */
    public static boolean isAllBlank(String... objs) {
        if (objs == null) {
            return true;
        }
        for (String obj : objs) {
            if (isNotBlank(obj)) {
                return false;
            }
        }
        return true;
    }

    /**
     * isIn(判断dest是否在srcs中)
     *
     * @param dest 目标对象
     * @param srcs 比较对象组
     * @return boolean
     */
    public static boolean isIn(String dest, String... srcs) {
        if (dest == null || srcs == null) {
            return false;
        }
        for (String src : srcs) {
            if (dest.equals(src)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断dest是否在srcs中，忽略大小写
     * 在CommonHelper也存在，后续删除。
     *
     * @param dest
     * @param srcs
     * @return
     * @create: 2017年11月9日 下午3:45:47
     * @author: wangyanfeng
     */
    public static boolean isInIgnoreCase(String dest, String... srcs) {
        if (dest == null || srcs == null) {
            return false;
        }
        for (String src : srcs) {
            if (dest.equalsIgnoreCase(src)) {
                return true;
            }
        }
        return false;
    }

    /**
     * isContains(判断dest是否包含Srcs的内容)
     *
     * @param dest 目标对象
     * @param srcs 比较对象组
     * @return boolean
     */
    public static boolean isContains(String dest, String... srcs) {
        if (dest == null || srcs == null) {
            return false;
        }
        for (String src : srcs) {
            if (dest.contains(src)) {
                return true;
            }
        }
        return false;
    }
}
