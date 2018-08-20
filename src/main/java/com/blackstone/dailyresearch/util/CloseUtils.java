package com.blackstone.dailyresearch.util;

import java.io.Closeable;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import org.apache.log4j.Logger;

/**
 * desc: 对象关闭通用方法
 *
 * @author 王彦锋
 * @date 2017/12/5 10:08
 */
final public class CloseUtils {
    private static final Logger LOG = Logger.getLogger(CloseUtils.class);

    /**
     * 禁止实例化
     */
    private CloseUtils() {}

    /**
     * 关闭 Statement 对象
     *
     * @param obj
     * @author 王彦锋
     * @date 2017/12/5 10:09
     */
    public static void close(Statement obj) {
        if (obj != null) {
            try {
                obj.close();
            } catch (Exception e) {
                LOG.error(obj.toString() + "close error", e);
            }
        }
    }
    public static void close(ResultSet obj) {
        if (obj != null) {
            try {
                obj.close();
            } catch (Exception e) {
                LOG.error(obj.toString() + "close error", e);
            }
        }
    }
    public static void close(Connection obj) {
        if (obj != null) {
            try {
                obj.close();
            } catch (Exception e) {
                LOG.error(obj.toString() + "close error", e);
            }
        }
    }

    /**
     * 关闭Closeable对象
     *
     * @param objs
     * @author 王彦锋
     * @date 2017/12/5 10:09
     */
    public static void close(Closeable... objs) {
        if (objs != null) {
            for (Closeable obj : objs) {
                try {
                    if (obj != null) {
                        obj.close();
                    }
                } catch (Exception e) {
                    LOG.error(obj + " close error", e);
                }
            }
        }
    }

    /**
     * 关闭对象，调用close方法
     *
     * @param objs
     * @author 王彦锋
     * @date 2017/12/11 20:23
     */
    public static void close(Object... objs) {
        if (objs != null) {
            for (Object obj : objs) {
                try {
                    doClose(obj);
                } catch (Exception e) {
                    LOG.error(obj.toString() + "close error", e);
                }
            }
        }
    }

    /**
     * 利用反射机制关闭对象
     *
     * @param instance
     * @return
     * @author 王彦锋
     * @date 2017/12/5 10:18
     */
    private static Object doClose(Object instance) {
        try {
            String methodName = "close";
            LOG.info("instance:" + instance.getClass().getName() + "-->" + instance.toString());
            Class<?> currentClazz = instance.getClass();
            Method callMethod = getMethod(methodName, currentClazz.getDeclaredMethods());
            while (callMethod == null) {
                currentClazz = currentClazz.getSuperclass();
                if (currentClazz != null) {
                    callMethod = getMethod(methodName, currentClazz.getDeclaredMethods());
                } else {
                    break;
                }
            }

            if (callMethod != null) {
                return callMethod.invoke(instance);
            } else {
                return "close method not fund : " + methodName;
            }
        } catch (Exception e) {
            LOG.error(e);
            return Arrays.deepToString(e.getStackTrace());
        }
    }

    /**
     * 根据方法名获取对应的方法对象
     *
     * @param methodName
     * @param methods
     * @return
     * @author 王彦锋
     * @date 2017/12/5 10:19
     */
    private static Method getMethod(String methodName, Method[] methods) {
        Method callMethod = null;
        for (Method method : methods) {
            LOG.debug("name of method -->" + method.getName());
            if (method.getName().equals(methodName)) {
                callMethod = method;
                callMethod.setAccessible(true);
                break;
            }
        }
        return callMethod;
    }

}
