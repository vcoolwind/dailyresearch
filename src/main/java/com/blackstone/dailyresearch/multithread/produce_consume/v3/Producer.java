package com.blackstone.dailyresearch.multithread.produce_consume.v3;

import java.util.concurrent.ConcurrentLinkedQueue;

public class Producer   {
    private ConcurrentLinkedQueue<String> queue;

    public Producer(ConcurrentLinkedQueue<String> queue, String tname) {
        this.queue = queue;
    }

    public void produce() {
        for (int i = 0; i < 1000; i++) {
            String target = "msg" + i;
                queue.add(target);
        }
    }
}
