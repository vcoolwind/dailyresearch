package com.balckstone.dailyresearch.bigdata.tempraturesort.v3;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class TSortReducerV3 extends Reducer<Text, IntWritable, Text, IntWritable> {
    @Override
    protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
        int temp = 0;
        for (IntWritable value : values) {
            if (value.get() > temp) {
                temp = value.get();
            }
        }
        context.write(key, new IntWritable(temp));
    }
}
