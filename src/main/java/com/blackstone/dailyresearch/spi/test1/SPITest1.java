package com.blackstone.dailyresearch.spi.test1;

public class SPITest1 {
    public static void main(String[] args) {
        try {
            Class.forName(SMSSender.class.getName());
            Class.forName(WXSender.class.getName());

            Driver wxDriver = new Driver("wx");
            wxDriver.send("haha");

            Driver smsDrivere = new Driver("sms");
            smsDrivere.send("haha");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
