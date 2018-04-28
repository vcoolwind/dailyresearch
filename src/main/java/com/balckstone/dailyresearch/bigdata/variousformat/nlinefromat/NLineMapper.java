package com.balckstone.dailyresearch.bigdata.variousformat.nlinefromat;

import java.io.IOException;
import java.util.Random;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class NLineMapper extends Mapper<LongWritable, Text, NullWritable, Text> {

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        Random random = new Random();
        int part = random.nextInt(4);
        context.write(NullWritable.get(), new Text(value.toString() + "\t" + part));
    }
}
