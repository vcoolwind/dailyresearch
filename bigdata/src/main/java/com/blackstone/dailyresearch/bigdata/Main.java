package com.blackstone.dailyresearch.bigdata;

import com.blackstone.dailyresearch.bigdata.hdfsfile.MapFileTest;
import com.blackstone.dailyresearch.bigdata.hdfsfile.SeqFileTest;
import com.blackstone.dailyresearch.bigdata.tempraturesort.v1.TSortMainV1;
import com.blackstone.dailyresearch.bigdata.tempraturesort.v1.TSortMainV1_2;
import com.blackstone.dailyresearch.bigdata.tempraturesort.v2.TSortMainV2;
import com.blackstone.dailyresearch.bigdata.tempraturesort.v3.TSortMainV3;
import com.blackstone.dailyresearch.bigdata.tempraturesort.v4.TSortMainV4;
import com.blackstone.dailyresearch.bigdata.variousformat.kvtextformat.MyKVTextFormatMain;
import com.blackstone.dailyresearch.bigdata.variousformat.nlinefromat.NLineMain;
import com.blackstone.dailyresearch.bigdata.variousformat.sequenceformat.SeqMain;
import com.blackstone.dailyresearch.bigdata.variousformat.textformat.MyTextFormatMain;
import com.blackstone.dailyresearch.bigdata.variousformat.xmlformat.XmlMain;
import com.blackstone.dailyresearch.bigdata.wordcount.WordCountMain;
import com.blackstone.dailyresearch.bigdata.wordcountsort.WordCountSortMain;
import com.blackstone.dailyresearch.bigdata.wordcountsort2.WordCountSort2Main;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
        put(new WordCountMain());
        put(new WordCountSortMain());
        put(new WordCountSort2Main());
        put(new XmlMain());
        put(new MyTextFormatMain());
        put(new MyKVTextFormatMain());
        put(new NLineMain());
        put(new SeqMain());
        put(new TSortMainV1());
        put(new TSortMainV1_2());
        put(new TSortMainV2());
        put(new TSortMainV3());
        put(new TSortMainV4());
    }

    private static void put(Runner runner) {
        runners.put(runner.getClass().getSimpleName(), runner);
        runners.put(runner.getClass().getName(), runner);
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
