package com.blackstone.dailyresearch.multithread.produce_consume.v3;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

public class Runner {
    public static void main(String[] args) {
        //无界队列，读取更快。但由于写入没有限制，容易出现内存问题。
        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
        for (int i = 1; i < 2; i++) {
            Producer producer = new Producer(queue, "P" + i);
            producer.produce();
        }
        int consumers=5;

        CountDownLatch latch =new CountDownLatch(consumers);
        for (int i = 0; i < consumers; i++) {
            Consumer consumer = new Consumer(queue, "C" + i,latch);
            consumer.start();
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("消费完成");
    }
}
