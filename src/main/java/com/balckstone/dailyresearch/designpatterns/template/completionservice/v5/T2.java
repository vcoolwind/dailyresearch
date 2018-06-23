package com.balckstone.dailyresearch.designpatterns.template.completionservice.v5;

import java.util.LinkedList;
import java.util.List;

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
     * 求N个数字的平方
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
                    void handleTask() {
                        for (final Integer origin : reqList) {
                            addTask(new Runner<Model<Integer>>() {
                                @Override
                                public Model<Integer> run() throws Exception {
                                    return doJob(origin);
                                }
                            });
                        }
                    }
                    @Override
                    public void handleResult(Model<Integer> result) {
                        retList.add(result);
                    }
                });

        return retList;
    }

    private Model<Integer> doJob(int  origin) {
        try {
            //模拟生产，执行时间延长1毫秒
            Thread.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Model<Integer> model = new Model<>(origin);
        model.setDest(origin*origin);
        return  model;
    }

}

