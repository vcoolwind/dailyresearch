package com.blackstone.dailyresearch.spi.test1;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Driver {
    private String protocol;
    private static Map<String, Class<? extends ISender>> map = new ConcurrentHashMap<String, Class<? extends ISender>>();
    private ISender targetInsance = null;

    public static void register(String name, Class<? extends ISender> clazz) {
        map.put(name, clazz);
    }

    public Driver(String protocol) {
        this.protocol = protocol;
    }

    public void send(String sth) {
        try {
            if (targetInsance == null&&map.containsKey(protocol)) {
                targetInsance = map.get(protocol).newInstance();
            }
            if (targetInsance == null) {
                System.out.println("没有对应的实现类");
                return;
            } else {
                targetInsance.send(sth);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
