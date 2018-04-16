package com.balckstone.dailyresearch.bigdata;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.balckstone.dailyresearch.bigdata.hdfsfile.MapFileTest;
import com.balckstone.dailyresearch.bigdata.hdfsfile.SeqFileTest;

/**
 * desc: 测试入口
 *
 * @author 王彦锋
 * @date 2018/4/16 15:01
 */
public class Main {
    private static Map<String, Runner> runners = new HashMap<>();

    static {
        put(new SeqFileTest());
        put(new MapFileTest());
    }

    private static void put(Runner runner) {
        runners.put(runner.getClass().getSimpleName(), runner);
    }

    public static void main(String[] args) throws Exception {
        System.out.println(Arrays.toString(args));
        int exitCode = -1;
        if (args == null || args.length < 1) {
            System.out.println("args error");
            System.exit(exitCode);
        }
        String mainCls = args[0];
        if (runners.containsKey(mainCls)) {
            String[] sargs = null;
            if (args.length > 1) {
                sargs = new String[args.length - 1];
                for (int i = 1; i < args.length; i++) {
                    sargs[i - 1] = args[i];
                }
            }
            exitCode = runners.get(mainCls).run(sargs);
            System.exit(exitCode);
        } else {
            System.out.println("Cann't find class :" + mainCls);
            System.exit(exitCode);
        }
    }
}
