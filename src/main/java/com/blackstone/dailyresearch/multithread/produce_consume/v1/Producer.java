package com.blackstone.dailyresearch.multithread.produce_consume.v1;


import java.util.List;
import java.util.Queue;

public class Producer extends Thread {
    private Queue<String> queue;

    public Producer(Queue<String> queue, String tname) {
        this.queue = queue;
        this.setName(tname);
    }

    @Override
    public void run() {
        for (int i = 0; i < 1000; i++) {
            synchronized (queue) {
                while (queue.size() >= 10) {
                    try {
                        //System.out.println(getName() + " 进入等待-------");
                        queue.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //System.out.println(getName() + " 进入执行");
                String target = "msg" + i;
                queue.add(target);
                //System.out.println(getName() + " 生产" + target);

                queue.notifyAll();
                //System.out.println(getName() + " 通知释放");
            }
            try {
                sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
