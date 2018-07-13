package com.blackstone.dailyresearch.rpc.test;

import com.blackstone.dailyresearch.rpc.RpcFramework;

/**
 * Created by BlackStone on 2016/12/30.
 */
public class RpcProvider {
    public static void main(String[] args) throws Exception {
        RpcFramework.export(new Object[]{new HelloServiceImpl(),new MathServiceImpl()}, 1234);
    }
}
