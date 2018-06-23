package com.balckstone.dailyresearch.designpatterns.template.completionservice.v1;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.balckstone.dailyresearch.designpatterns.template.completionservice.Model;
import org.apache.commons.lang.StringUtils;

public class T1Simple {
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

    public static void main(String[] args) {
        List<Model<String>> reqList = new LinkedList<>();
        int times = 10000;
        for (int i = 0; i < times; i++) {
            reqList.add(new Model(getRandomString(96)));
        }
        long now = System.currentTimeMillis();
        T1Simple t1Simple = new T1Simple();
        List<Model<String>> respList = t1Simple.execute(reqList);
        System.out.println("expired time:" + (System.currentTimeMillis() - now));
        //System.out.println(respList);
    }

    /**
     * 对N多个字符串进行反转，并输出反转前后的结果。
     *
     * @param arrs
     */
    public List<Model<String>> execute(List<Model<String>> arrs) {
        List<Model<String>> retList = new LinkedList<>();
        for (Model<String> model : arrs) {
            model.setDest(reverse(model.getOrigin()));
            retList.add(model);
        }
        return retList;
    }

    private String reverse(String origin) {
        try {
            //模拟生产，执行时间延长1毫秒
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return StringUtils.reverse(origin);
    }

}

