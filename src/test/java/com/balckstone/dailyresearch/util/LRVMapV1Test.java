package com.balckstone.dailyresearch.util;

import java.util.Random;
import org.junit.Test;

public class LRVMapV1Test {
    LRVMapV1<Integer, String> lruMap = new LRVMapV1(10);

    @Test()
    public void allTest() {
        for (int i = 1; i < 11; i++) {
            lruMap.put(i, String.valueOf(i));
        }
        showInfo();


        for (int i = 1; i < 11; i++) {
            lruMap.get(i);
            showInfo();
        }

        Random random = new Random();
        for (int i = 1; i < 11; i++) {
            int t = random.nextInt(11);
            System.out.println("remove "+t);
            lruMap.remove(t);
            showInfo();
        }

        lruMap.clear();
        showInfo();
    }


    private void showInfo() {
        System.out.println("------" + lruMap.size() + "-------");
        lruMap.showFromHead();
        lruMap.showFromTail();
    }

}