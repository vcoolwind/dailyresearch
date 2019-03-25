package com.blackstone.dailyresearch.jdbc;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import com.blackstone.dailyresearch.util.ConsoleLog;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

public class Expense {
    public static void main(String[] args) throws Exception {
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(10000);
        ThreadPoolExecutor.CallerRunsPolicy policy = new ThreadPoolExecutor.CallerRunsPolicy();
        ExecutorService executor = new ThreadPoolExecutor(5, 5, 0L, MILLISECONDS, workQueue, policy);
        CompletionService<String> completionService = new ExecutorCompletionService<String>(executor);
        int taskNum = 0;
        String start = "20181008";
        String end = "20181101";

        String current = start;

        while (true) {
            JDBCHelper jdbc1 = new JDBCHelper();
            final String nextDay = jdbc1.getNextWorkDay(current, 1);

            final String exeDay = current;
            ConsoleLog.println("---(" + exeDay + "," + nextDay + ")");
            if (Integer.parseInt(current) < Integer.parseInt(end)) {
                completionService.submit(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        JDBCHelper jdbc = new JDBCHelper();
                        return jdbc.callExpenseProc(exeDay, nextDay);
                    }
                });
                taskNum++;
            } else {
                break;
            }
            current = nextDay;
        }
        ConsoleLog.println("taskNum:" + taskNum);

        for (int i = 0; i < taskNum; i++) {
            String ret = completionService.take().get();
            ConsoleLog.println(ret);
        }

        executor.shutdown();

    }
}
