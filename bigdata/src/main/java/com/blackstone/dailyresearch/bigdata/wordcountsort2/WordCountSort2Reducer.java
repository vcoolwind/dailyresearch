package com.blackstone.dailyresearch.bigdata.wordcountsort2;

import java.io.IOException;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * @author vcoolwind
 */
public class WordCountSort2Reducer extends Reducer<IntWritable,Text,Text,IntWritable> {
    @Override
    protected void reduce(IntWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        for (Text value : values) {
            context.write(value,key);
        }
    }
}
