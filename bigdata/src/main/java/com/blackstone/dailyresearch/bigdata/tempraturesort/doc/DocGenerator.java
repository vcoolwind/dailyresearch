package com.blackstone.dailyresearch.bigdata.tempraturesort.doc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Random;

/**
 * desc:
 *
 * @author 王彦锋
 * @date 2018/5/7 14:07
 */
public class DocGenerator {
    public static void main(String[] args) throws IOException {
        Random random = new Random();
        for (int i = 1; i < 11; i++) {
            String path = "d:/d" + i + ".csv";
            File fout = new File(path);
            FileOutputStream fos = new FileOutputStream(fout);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos, "utf-8"));
            for (int j = 0; j < 10000; j++) {
                int year = 1900 + random.nextInt(118);
                int month = 1 + random.nextInt(12);
                int day = 1 + random.nextInt(30);
                int temperature = -15 + random.nextInt(55);
                String weather = random.nextBoolean() ? "晴天" : "阴天";
                String line = year + "/" + month + "/" + day + "," + weather + "," + temperature;
                bw.write(line);
                bw.write("\n");
            }

            bw.close();

        }


    }
}
