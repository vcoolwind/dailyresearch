package com.balckstone.dailyresearch.designpatterns.template.v4;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import com.balckstone.dailyresearch.designpatterns.template.Model;

public class T2 {

    public static void main(String[] args) throws Exception {
        List<Model<Integer>> reqList = new LinkedList<>();
        int times = 10;
        for (int i = 1; i < times; i++) {
            reqList.add(new Model(i));
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
    public List<Model<Integer>> execute(final List<Model<Integer>> reqList) throws Exception {
        final List<Model<Integer>> retList = new LinkedList<>();
        int threadNum = reqList.size() / 1000 + 1;
        //生成任务，提交线程池处理。
        CompletionServiceTemplate.getInstance().execute(threadNum, reqList.size(),
                new CompletionServiceCallback<Model<Integer>>() {
                    @Override
                    void handleTask() {
                        for (final Model<Integer> model : reqList) {
                            addCallable(new Callable<Model<Integer>>() {
                                @Override
                                public Model<Integer> call() throws Exception {
                                    model.setDest(doJob(model.getOrigin()));
                                    return model;
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

    private int doJob(int origin) {
        try {
            //模拟生产，执行时间延长1毫秒
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return origin * origin;
    }

}

