package com.balckstone.dailyresearch.designpatterns.template.v3;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

import com.balckstone.dailyresearch.designpatterns.template.Model;
import org.apache.commons.lang.StringUtils;

public class T1 {
    public static String getRandomString(int length) {
        //定义一个字符串（A-Z，a-z，0-9）即62位；
        String str = "zxcvbnmlkjhgfdsaqwertyuiopQWERTYUIOPASDFGHJKLZXCVBNM1234567890";
        //由Random生成随机数
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        //长度为几就循环几次
        for (int i = 0; i < length; ++i) {
            //产生0-61的数字
            int number = random.nextInt(62);
            //将产生的数字通过length次承载到sb中
            sb.append(str.charAt(number));
        }
        //将承载的字符转换成字符串
        return sb.toString();
    }

    public static void main(String[] args) throws Exception {
        List<Model<String>> reqList = new LinkedList<>();
        int times = 10000;
        for (int i = 0; i < times; i++) {
            reqList.add(new Model(getRandomString(96)));
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
    public List<Model<String>> execute(final List<Model<String>> reqList) throws Exception {
        final List<Model<String>> retList = new LinkedList<>();
        int threadNum = reqList.size() / 1000 + 1;
        //生成任务，提交线程池处理。
        CompletionServiceTemplate.getInstance().execute(threadNum, reqList.size(),
                new CompletionServiceCallback<Model<String>>() {
                    @Override
                    public List<Callable<Model<String>>> genCallables() {
                        List<Callable<Model<String>>> callList = new ArrayList<>();
                        for (final Model<String> model : reqList) {
                            callList.add(new Callable<Model<String>>() {
                                @Override
                                public Model<String> call() throws Exception {
                                    model.setDest(doTask(model.getOrigin()));
                                    return model;
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

    private String doTask(String origin) {
        try {
            //模拟生产，执行时间延长1毫秒
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return StringUtils.reverse(origin);
    }

}

