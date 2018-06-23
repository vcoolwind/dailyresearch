package com.balckstone.dailyresearch.designpatterns.template.completionservice.v2;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.balckstone.dailyresearch.designpatterns.template.completionservice.Model;

public class T2 {

    public static void main(String[] args) throws Exception {
        List<Integer> reqList = new LinkedList<>();
        int times = 10;
        for (int i = 1; i < times; i++) {
            reqList.add(i);
        }
        long now = System.currentTimeMillis();
        T2 t2 = new T2();
        List<Model<Integer>> respList = t2.execute(reqList);
        System.out.println("expired time:" + (System.currentTimeMillis() - now));
        System.out.println(respList);
    }

    /**
     * 对N数字求平方。
     *
     * @param reqList
     */
    public List<Model<Integer>> execute(List<Integer> reqList) throws Exception {
        List<Model<Integer>> retList = new LinkedList<>();
        int threadNum = reqList.size() / 1000 + 1;
        ThreadPoolExecutor executor = new ThreadPoolExecutor(threadNum, threadNum, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(reqList.size()), new ThreadPoolExecutor.CallerRunsPolicy());
        CompletionService<Model<Integer>> completionService = new ExecutorCompletionService<Model<Integer>>(executor);
        int taskNum = 0;
        //生成任务，提交线程池处理。
        for (final Integer origin : reqList) {
            completionService.submit(new Callable<Model<Integer>>() {
                @Override
                public Model<Integer> call() throws Exception {
                    return doJob(origin);
                }
            });
            taskNum++;
        }
        //每个任务生成一行数据，添加到目标数据集。
        for (int i = 0; i < taskNum; i++) {
            Model<Integer> ret = completionService.take().get();
            retList.add(ret);
        }
        executor.shutdown();
        return retList;
    }

    private Model<Integer> doJob(int origin) {
        try {
            //模拟生产，执行时间延长1毫秒
            Thread.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Model<Integer> model = new Model<>(origin);
        model.setDest(origin * origin);
        return model;
    }

}

