package com.balckstone.dailyresearch.bigdata.variousformat.nlinefromat;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class NLineReducer extends Reducer<NullWritable, IntWritable, IntWritable, IntWritable> {

    // https://blog.csdn.net/doegoo/article/details/50969028
    // https://blog.csdn.net/doegoo/article/details/50392109
    @Override
    protected void reduce(IntWritable key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {

    }
}
