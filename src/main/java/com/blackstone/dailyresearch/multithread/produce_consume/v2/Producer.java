package com.blackstone.dailyresearch.multithread.produce_consume.v2;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Producer extends Thread {
    private LinkedBlockingQueue<String> queue;

    public Producer(LinkedBlockingQueue<String> queue, String tname) {
        this.queue = queue;
        this.setName(tname);
    }

    @Override
    public void run() {
        for (int i = 0; i < 1000; i++) {
            String target = "msg" + i;
            try {
                queue.put(target);
                sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
