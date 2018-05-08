package com.balckstone.dailyresearch.bigdata.tempraturesort.v4;

import java.io.IOException;

import com.balckstone.dailyresearch.bigdata.io.IntPair;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class TSortReducerV4 extends Reducer<IntPair, NullWritable, IntPair, NullWritable> {
    @Override
    protected void reduce(IntPair key, Iterable<NullWritable> values, Context context) throws IOException, InterruptedException {

        context.write(key,NullWritable.get());
    }
}
