package com.blackstone.dailyresearch.designpatterns.template.completionservice.v2;

import static com.blackstone.dailyresearch.util.StringHelper.getRandomString;

import com.blackstone.dailyresearch.designpatterns.template.completionservice.Model;
import com.blackstone.dailyresearch.util.StringHelper;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang.StringUtils;

public class T1 {


    public static void main(String[] args) throws Exception {
        List<String> reqList = new LinkedList<>();
        int times = 10000;
        for (int i = 0; i < times; i++) {
            reqList.add(StringHelper.getRandomString(96));
        }
        long now = System.currentTimeMillis();
        T1 t1 = new T1();
        List<Model<String>> respList = t1.execute(reqList);
        System.out.println("expired time:" + (System.currentTimeMillis() - now));
        //System.out.println(respList);
    }

    /**
     * 对N多个字符串进行反转，并输出反转前后的结果。
     *
     * @param reqList
     */
    public List<Model<String>> execute(List<String> reqList) throws Exception {
        List<Model<String>> retList = new LinkedList<>();
        int threadNum = reqList.size() / 1000 + 1;
        ThreadPoolExecutor executor = new ThreadPoolExecutor(threadNum, threadNum, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(reqList.size()), new ThreadPoolExecutor.CallerRunsPolicy());
        CompletionService<Model<String>> completionService = new ExecutorCompletionService<Model<String>>(executor);
        int taskNum = 0;
        //生成任务，提交线程池处理。
        for (final String origin : reqList) {
            completionService.submit(new Callable<Model<String>>() {
                @Override
                public Model<String> call() throws Exception {
                    return doJob(origin);
                }
            });
            taskNum++;
        }
        //每个任务生成一行数据，添加到目标数据集。
        for (int i = 0; i < taskNum; i++) {
            Model<String> ret = completionService.take().get();
            retList.add(ret);
        }
        executor.shutdown();
        return retList;
    }

    private Model<String> doJob(String origin) {
        try {
            //模拟生产，执行时间延长1毫秒
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Model<String> model = new Model<>(origin);
        model.setDest(StringUtils.reverse(origin));
        return model;
    }

}

