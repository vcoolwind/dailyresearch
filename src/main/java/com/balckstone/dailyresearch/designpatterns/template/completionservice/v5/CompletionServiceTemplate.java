package com.balckstone.dailyresearch.designpatterns.template.completionservice.v5;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.log4j.Logger;

/**
 * 使用线程池执行批量任务，同时回调产生的结果。
 * @param <V> 任务执行返回值
 * @author 王彦锋
 * @date 2018/6/22 20:53
 *
 */
public class CompletionServiceTemplate<V> {
    private static final Logger LOG = Logger.getLogger(CompletionServiceTemplate.class);

    private static CompletionServiceTemplate instance = new CompletionServiceTemplate();

    /**
     * 标准的懒汉模式。
     */
    private CompletionServiceTemplate() {
    }

    /**
     * 返回CompletionServiceTemplate实例。
     * @create: 2017年8月26日 下午12:00:22
     * @author: wangyanfeng
     * @return CacheUpdateTemplate
     */
    public static CompletionServiceTemplate getInstance() {
        return instance;
    }

    /**
     * 指定一定数量线程，并发完成callback对应的任务
     * @param threadNum 启用的线程数
     * @param capacity 任务缓存容量
     * @param callback 任务回调类
     * @throws Exception
     */
    public void execute(final int threadNum, final int capacity, final CompletionServiceCallback callback)
            throws Exception {
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(capacity);
        ThreadPoolExecutor.CallerRunsPolicy policy = new ThreadPoolExecutor.CallerRunsPolicy();
        ExecutorService executor = new ThreadPoolExecutor(threadNum, threadNum, 0L, MILLISECONDS, workQueue, policy);
        CompletionService<V> completionService = new ExecutorCompletionService<V>(executor);
        List<Callable<V>> list = callback.getCallables();
        int taskNum = 0;
        for (Callable<V> callable : list) {
            completionService.submit(callable);
            taskNum++;
        }
        for (int i = 0; i < taskNum; i++) {
            V ret = completionService.take().get();
            callback.handleResult(ret);
        }
        executor.shutdown();
    }
}
