package com.balckstone.dailyresearch.util;

import java.util.Random;
import org.junit.Test;

public class LRUMapV1Test {
    LRUMapV1<Integer, String> lruMap = new LRUMapV1(10);

    @Test()
    public void allTest() {
        for (int i = 1; i < 11; i++) {
            lruMap.put(i, String.valueOf(i));
        }
        showInfo();
        Random random = new Random();

        for (int i = 10; i >0; i--) {
            lruMap.get(i);
            showInfo();
        }
        for (int i = 1; i <11; i=i+2) {
            lruMap.get(i);
            showInfo();
        }


//        for (int i = 1; i < 11; i++) {
//            int t = random.nextInt(11);
//            System.out.println("remove "+t);
//            lruMap.remove(t);
//            showInfo();
//        }
//
//        lruMap.clear();
//        showInfo();
    }


    private void showInfo() {
        System.out.println("------" + lruMap.size() + "-------");
        lruMap.showFromHead();
        lruMap.showFromTail();
    }

}