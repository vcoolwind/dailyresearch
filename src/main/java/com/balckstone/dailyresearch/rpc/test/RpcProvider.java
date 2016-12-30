package com.balckstone.dailyresearch.rpc.test;

import com.balckstone.dailyresearch.rpc.RpcFramework;

/**
 * Created by BlackStone on 2016/12/30.
 */
public class RpcProvider {
    public static void main(String[] args) throws Exception {
        RpcFramework.export(new Object[]{new HelloServiceImpl(),new MathServiceImpl()}, 1234);
    }
}
