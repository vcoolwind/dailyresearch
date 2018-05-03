package com.balckstone.dailyresearch.bigdata.tempraturesort.v1;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class TSortReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
    @Override
    protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
        int temp = 0;
        for (IntWritable value : values) {
            if (value.get() > temp) {
                temp = value.get();
            }
            context.write(key, new IntWritable(value.get()));
        }

    }
}
