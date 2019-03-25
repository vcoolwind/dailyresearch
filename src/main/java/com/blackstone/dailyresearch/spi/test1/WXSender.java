package com.blackstone.dailyresearch.spi.test1;

public class WXSender implements ISender {
    static {
        Driver.register("wx", WXSender.class);
    }

    @Override
    public void send(String msg) {
        System.out.println("Send msg[" + msg + "] with " + getClass().getName());
    }
}
