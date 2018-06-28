package com.balckstone.dailyresearch.designpatterns.template.cacheupdate;

import java.util.Calendar;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

/**
 * 数据缓存更新模板
 * @create: 2017年8月25日 下午4:09:45
 * @author: wangyanfeng
 *
 */
public class CacheUpdateTemplate {
    private static final Logger LOG = Logger.getLogger(CacheUpdateTemplate.class);

    private static CacheUpdateTemplate instance = new CacheUpdateTemplate();

    private static ConcurrentMap<String, Lock> lockMap = new ConcurrentHashMap<String, Lock>();

    /**
     * 标准的懒汉模式。
     */
    private CacheUpdateTemplate() {
    }

    /**
     * 返回CacheUpdateTemplate实例。
     * @create: 2017年8月26日 下午12:00:22
     * @author: wangyanfeng
     * @return CacheUpdateTemplate
     */
    public static CacheUpdateTemplate getInstance() {
        return instance;
    }

    /**
     * 通用的执行缓存更新方法，lock-->doAction-->unlock
     * @create: 2017年8月25日 下午6:37:30
     * @author: wangyanfeng
     * @param expiredTime 缓存超时时间
     * @param forceUpdate 是否强制更新
     * @param action UpdateCallback
     */
    public void execute(final long expiredTime, final boolean forceUpdate, final UpdateCallback action) {
        String cacheKey = getCacheKey();
        LOG.info("check update with cacheKey : " + cacheKey);
        Long currTimeInMillis = Calendar.getInstance().getTimeInMillis();
        Long lastUpdTime = getLastUpdateTime(cacheKey);
        LOG.info(String.format("(currTimeInMillis - lastUpdTime) > expiredTime || forceUpdate:(%s - %s) > %s || %s", currTimeInMillis, lastUpdTime, expiredTime,
                forceUpdate));
        if ((currTimeInMillis - lastUpdTime) >= expiredTime || forceUpdate) {
            Lock locker = lockMap.get(cacheKey);
            if (locker == null) {
                locker = new ReentrantLock();
                lockMap.put(cacheKey, locker);
            }
            LOG.info("try to get lock");
            locker.lock();
            LOG.info("success get lock");
            long start = System.currentTimeMillis();

            try {
                LOG.info("try to do update");
                if (start - getLastUpdateTime(cacheKey) < expiredTime) {
                    LOG.info("update by other thread,ignore!");
                } else {
                    action.doUpdate();
                    LOG.info("do update end ,expired with millis:" + (System.currentTimeMillis() - start));
                    setLastUpdateTime(cacheKey, currTimeInMillis);
                }
            } finally {
                locker.unlock();
                LOG.info("finally unlock");
            }
        }
    }

    /**
     * 获得当前更新key， 类名+方法名
     * @create: 2017年8月25日 下午4:56:10
     * @author: wangyanfeng
     * @return
     */
    private String getCacheKey() {
        String lockName = null;
        try {
            // 获取到模板方法最初的调用者 2层调用，使用[3]
            String clsName = Thread.currentThread().getStackTrace()[3].getClassName();
            String methodName = Thread.currentThread().getStackTrace()[3].getMethodName();
            lockName = clsName + "." + methodName;
        } catch(Exception e) {
            lockName = "default_lock";
        }
        return lockName;
    }

    /**
     * 缓存最新的更新时间
     */
    private static ConcurrentMap<String, Long> lastUpdateTimeMap = new ConcurrentHashMap<String, Long>();

    /**
     * 获取最新的缓存时间
     * @create: 2017年8月25日 下午5:23:40
     * @author: wangyanfeng
     * @param cacheKey
     * @return
     */
    private long getLastUpdateTime(String cacheKey) {
        Long lastTime = lastUpdateTimeMap.get(cacheKey);
        if (lastTime == null) {
            lastTime = 0L;
            lastUpdateTimeMap.put(cacheKey, lastTime);
        }
        return lastTime;
    }

    private void setLastUpdateTime(final String cacheKey, final Long updTime) {
        lastUpdateTimeMap.put(cacheKey, updTime);
    }

}

