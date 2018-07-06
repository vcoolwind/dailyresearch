package com.balckstone.dailyresearch.mongotest;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MonoRWTest {
    private static String collName = "wyf_test";
    private static void doWrite() {
        final  SimpleDateFormat sdf=new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");

        for (int j = 0; j < 1; j++) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    int i = 0;
                    while (true) {
                        i++;
                        final MongoService mongoService = new MongoService();

                        final DBCollection collection = mongoService.getCollection(collName);
                        mongoService.createIndexs(collection,"dt");
                        Map<String, Object> dataMap = new HashMap<>(15);
                        dataMap.put("pos", i);
                        dataMap.put("dt", new Date());
                        dataMap.put("age", i + 10);
                        dataMap.put("add", "罗湖桃园路#" + i);
                        mongoService.save(collection, mongoService.buildDBObject(dataMap));
                        try {
                            Thread.sleep(new Random().nextInt(10)+2);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        if (i % 1000 == 0) {
                            System.out.println(sdf.format(new Date())+" "+Thread.currentThread().getName()+"完成写入1000条数据！");
                        }
                    }
                }
            });
            t.setName("WriteThread"+j);
            t.start();
        }
        System.out.println("Write start OK!");
    }

    private static void doRead() {

        for (int j = 0; j < 1; j++) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    int i = 1;
                    while (true) {
                        final MongoService mongoService = new MongoService();
                        final DBCollection collection = mongoService.getCollection(collName);
                        BasicDBObject field = new BasicDBObject("_id", 1);
                        field.append("dt_display1", 1).append("add", 1);

                        BasicDBObject sort = new BasicDBObject("dt", -1);

                        DBObject ret = collection.findOne(new BasicDBObject(), field, sort);
                        i++;
                        if (i % 1000 == 0) {
                            System.out.println(Thread.currentThread().getName()+"-->"+ret);
                        }
                        try {
                            Thread.sleep(new Random().nextInt(10)+2);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            t.setName("ReadThread"+j);
            t.start();
        }
        System.out.println("Read start OK!");
    }

    public static void main(String[] args) {
        doWrite();
        doRead();
    }
}
