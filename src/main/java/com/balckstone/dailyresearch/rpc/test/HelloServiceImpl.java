package com.balckstone.dailyresearch.rpc.test;

/**
 * Created by BlackStone on 2016/12/30.
 */
public class HelloServiceImpl implements HelloService {
    public String hello(String name) {
        return "Hello " + name;
    }
}
