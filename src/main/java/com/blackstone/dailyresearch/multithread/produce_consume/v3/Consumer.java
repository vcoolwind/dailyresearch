package com.blackstone.dailyresearch.multithread.produce_consume.v3;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

public class Consumer extends Thread {
    private ConcurrentLinkedQueue<String> queue;
    private CountDownLatch lanch;

    public Consumer(ConcurrentLinkedQueue<String> queue, String tname, CountDownLatch lanch) {
        this.lanch=lanch;
        this.queue = queue;
        this.setName(tname);
    }

    @Override
    public void run() {
        while (!queue.isEmpty()) {
            try {
                String target = queue.poll();
                System.out.println(getName() + " 消费" + target + " 剩余：" + queue.size());
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        lanch.countDown();
    }
}
