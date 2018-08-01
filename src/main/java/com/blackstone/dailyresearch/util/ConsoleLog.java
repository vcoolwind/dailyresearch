package com.blackstone.dailyresearch.util;

import static com.blackstone.dailyresearch.util.DateHelper.getCurrentDateTime;

public class ConsoleLog {
    public static void println(Object msg) {
        System.out.println(getCurrentDateTime() + ": " + msg);
    }
}
