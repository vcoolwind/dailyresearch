package com.balckstone.dailyresearch.designpatterns.template.completionservice.v3;

import static com.balckstone.dailyresearch.util.StringHelper.getRandomString;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import com.balckstone.dailyresearch.designpatterns.template.completionservice.Model;
import org.apache.commons.lang.StringUtils;

public class T1 {

    public static void main(String[] args) throws Exception {
        List<String> reqList = new LinkedList<>();
        int times = 10000;
        for (int i = 0; i < times; i++) {
            reqList.add(getRandomString(96));
        }
        long now = System.currentTimeMillis();
    T1 t1 =new T1();
        List<Model<String>> respList = t1.execute(reqList);
        System.out.println("expired time:" + (System.currentTimeMillis() - now));
        //System.out.println(respList);
    }


    /**
     * 对N多个字符串进行反转，并输出反转前后的结果。
     *
     * @param reqList
     */
    public List<Model<String>> execute(final List<String> reqList) throws Exception {
        final List<Model<String>> retList = new LinkedList<>();
        int threadNum = reqList.size() / 1000 + 1;
        //生成任务，提交线程池处理。
        CompletionServiceTemplate.getInstance().execute(threadNum, reqList.size(),
                new CompletionServiceCallback<Model<String>>() {
                    @Override
                    public List<Callable<Model<String>>> genCallables() {
                        List<Callable<Model<String>>> callList = new ArrayList<>();
                        for (final String origin : reqList) {
                            callList.add(new Callable<Model<String>>() {
                                @Override
                                public Model<String> call() throws Exception {
                                    return doJob(origin);
                                }
                            });
                        }
                        return callList;
                    }

                    @Override
                    public void handleResult(Model result) {
                        retList.add(result);
                    }
                });

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

