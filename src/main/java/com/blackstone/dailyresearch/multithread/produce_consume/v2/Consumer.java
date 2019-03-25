package com.blackstone.dailyresearch.multithread.produce_consume.v2;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Consumer extends Thread {
    private LinkedBlockingQueue<String> queue;

    public Consumer(LinkedBlockingQueue<String> queue, String tname) {
        this.queue = queue;
        this.setName(tname);
    }

    @Override
    public void run() {
        while (true) {
            try {
                String target = queue.take();
                System.out.println(getName() + " 消费" + target+" 剩余："+queue.size());
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
