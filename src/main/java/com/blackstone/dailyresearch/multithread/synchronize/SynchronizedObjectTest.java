package com.blackstone.dailyresearch.multithread.synchronize;

import java.util.ArrayList;
import java.util.List;

public class SynchronizedObjectTest {
    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            test();
        }
    }

    private static void test() throws InterruptedException {
        SynchronizedMethod method = new SynchronizedMethod();
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

    static class SynchronizedMethod {
        private List<Integer> list = new ArrayList<>();

        public synchronized void add(int i) {
            synchronized (list) {
                list.add(i);
            }
        }

        public void show() {
            System.out.println("list.size():" + list.size());
        }

    }
}
