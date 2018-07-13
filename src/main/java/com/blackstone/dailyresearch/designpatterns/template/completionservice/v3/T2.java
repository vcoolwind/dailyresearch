package com.blackstone.dailyresearch.designpatterns.template.completionservice.v3;

import com.blackstone.dailyresearch.designpatterns.template.completionservice.Model;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

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
     * 对N多个字符串进行反转，并输出反转前后的结果。
     *
     * @param reqList
     */
    public List<Model<Integer>> execute(final List<Integer> reqList) throws Exception {
        final List<Model<Integer>> retList = new LinkedList<>();
        int threadNum = reqList.size() / 1000 + 1;
        //生成任务，提交线程池处理。
        CompletionServiceTemplate.getInstance().execute(threadNum, reqList.size(),
                new CompletionServiceCallback<Model<Integer>>() {
                    @Override
                    public List<Callable<Model<Integer>>> genCallables() {
                        List<Callable<Model<Integer>>> callList = new ArrayList<>();
                        for (final Integer origin : reqList) {
                            callList.add(new Callable<Model<Integer>>() {
                                @Override
                                public Model<Integer> call() throws Exception {
                                    return doJob(origin);
                                }
                            });
                        }
                        return callList;
                    }

                    @Override
                    public void handleResult(Model<Integer> result) {
                        retList.add(result);
                    }
                });

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

