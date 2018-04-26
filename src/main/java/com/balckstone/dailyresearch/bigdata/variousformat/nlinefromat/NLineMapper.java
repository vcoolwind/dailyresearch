package com.balckstone.dailyresearch.bigdata.variousformat.nlinefromat;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class NLineMapper extends Mapper<LongWritable, Text, IntWritable, IntWritable> {
    private int lineNum = 0;
    private IntWritable okey = new IntWritable();

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        lineNum++;
        okey.set(lineNum);
        String[] values = value.toString().split(",");
        for (String s : values) {
            context.write(okey, new IntWritable(Integer.parseInt(s)));
        }
    }
}
