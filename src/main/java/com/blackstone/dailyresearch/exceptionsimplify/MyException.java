package com.blackstone.dailyresearch.exceptionsimplify;

public class MyException extends Exception {

    public MyException(){

    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        //抛弃堆栈，只作为业务异常处理。
        return null;
    }
}
