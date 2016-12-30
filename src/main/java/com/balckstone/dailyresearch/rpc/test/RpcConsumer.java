package com.balckstone.dailyresearch.rpc.test;

import com.balckstone.dailyresearch.rpc.RpcFramework;

/**
 * Created by BlackStone on 2016/12/30.
 */
public class RpcConsumer {
    public static void main(String[] args) throws Exception {
        HelloService service1 = RpcFramework.refer(HelloService.class, "127.0.0.1", 1234);
        MathService service12 = RpcFramework.refer(MathService.class,"127.0.0.1", 1234);
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            String hello = service1.hello("World" + i);
            System.out.println(hello);
            int sum = service12.sum(i,i);
            System.out.println(sum);

            Thread.sleep(1000);
        }
    }
}
