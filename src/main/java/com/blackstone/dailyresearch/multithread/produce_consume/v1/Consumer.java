package com.blackstone.dailyresearch.multithread.produce_consume.v1;


import java.util.List;
import java.util.Queue;

public class Consumer extends Thread {
    private Queue<String> queue;

    public Consumer(Queue<String> queue, String tname) {
        this.queue = queue;
        this.setName(tname);
    }

    @Override
    public void run() {
        while (true) {
            String target = null;
            synchronized (queue) {
                while (queue.isEmpty()) {
                    try {
                        //System.out.println(getName() + " 进入等待-----");
                        queue.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //System.out.println(getName() + "  进入执行");
                target = queue.remove();
                queue.notifyAll();
                //System.out.println(getName() + " 通知释放");
            }
            System.out.println(getName() + " 消费" + target);
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}

