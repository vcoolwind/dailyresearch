package com.blackstone.dailyresearch.spi.test1;

public class SMSSender implements ISender {
    static {
        Driver.register("sms", SMSSender.class);
    }

    @Override
    public void send(String msg) {
        System.out.println("Send msg[" + msg + "] with " + getClass().getName());
    }
}
