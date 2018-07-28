package com.blackstone.dailyresearch.util;

import static com.blackstone.dailyresearch.util.DateHelper.getCurrentDateTime;

public class ConsoleLog {
    public static void println(String msg) {
        System.out.println(getCurrentDateTime() + ": " + msg);
    }
}
