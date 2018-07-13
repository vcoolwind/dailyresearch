package com.blackstone.dailyresearch.bigdata.config;


import org.apache.hadoop.conf.Configuration;

public class ConfigurationTest {
    public static void main(String[] args) {
        Configuration configuration =new Configuration();
        configuration.addResource("configuration-1.xml");

        System.out.println("yellow".equals(configuration.get("color")));
        System.out.println("16".equals(configuration.get("size")));
        System.out.println(configuration.get("color-size"));

        //配置文件会被覆盖，但final的属性不会。
        configuration.addResource("configuration-2.xml");
        System.out.println("red".equals(configuration.get("color")));
        System.out.println("16".equals(configuration.get("size")));
        System.out.println("1024".equals(configuration.get("width")));

        System.out.println(configuration.get("color-size"));

    }
}
