package com.balckstone.dailyresearch.bigdata;

/**
 * @author vcoolwind
 */
public interface Runner {
    /**
     * 被main調用的方法
     * @param args
     * @return
     * @throws Exception
     */
    int run(String[] args) throws  Exception;
}
