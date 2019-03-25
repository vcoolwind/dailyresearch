package com.blackstone.dailyresearch.multithread.produce_consume.v2;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class Runner {
    public static void main(String[] args){
        LinkedBlockingQueue<String> queue=new LinkedBlockingQueue<>(10);
        for (int i = 1; i < 2; i++) {
            Producer producer=new Producer(queue,"P"+i);
            producer.start();
        }
        for (int i = 1; i < 5; i++) {
            Consumer consumer=new Consumer(queue,"C"+i);
            consumer.start();
        }
    }
}
