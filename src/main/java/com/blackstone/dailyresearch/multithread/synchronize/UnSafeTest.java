package com.blackstone.dailyresearch.multithread.synchronize;

import java.util.ArrayList;
import java.util.List;

public class UnSafeTest {
    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            test();
        }

    }

    private static void test() throws InterruptedException {
        UnsaveMethod method = new UnsaveMethod();
        for (int i = 0; i < 20; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < 100; j++) {
                        method.add(j);
                    }
                }
            }).start();
        }
        Thread.sleep(3000);
        method.show();
    }



        static class UnsaveMethod {
        private List<Integer> list = new ArrayList<>();

        /**
         * desc: 线程不安全，导致并发错误
         *
         * @author 王彦锋
         * @date 2019/3/23 18:33
         */
        public  void add(int i) {
            list.add(i);
        }

        public void show() {
            System.out.println("list.size():" + list.size());
        }

    }
}
