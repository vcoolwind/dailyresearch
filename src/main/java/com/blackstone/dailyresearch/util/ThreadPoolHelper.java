package com.blackstone.dailyresearch.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * Description: 处理可根据需求动态设置线程池参数
 * Author:   xiezf
 * Date:     2017/12/19 19:51
 */
public class ThreadPoolHelper {

    private static ThreadFactory threadFactory = null;

    /**
     * 生成带有配置的线程池
     * @threadName
     * @param corePoolSize
     * @param maximumPoolSize
     * @param keepAliveTime
     * @param unit
     * @param blockingQueueSize
     * @return
     */
    public static ExecutorService createThreadPool(String threadName, int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, int blockingQueueSize) {
        threadFactory = new ThreadFactoryBuilder().setNameFormat(threadName+"-pool-%d").build();
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit,
                new LinkedBlockingQueue<Runnable>(blockingQueueSize), threadFactory, new ThreadPoolExecutor.CallerRunsPolicy());

    }
}
