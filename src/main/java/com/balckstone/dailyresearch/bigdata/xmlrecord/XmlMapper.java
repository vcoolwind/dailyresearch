package com.balckstone.dailyresearch.bigdata.xmlrecord;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * desc:
 *
 * @author 王彦锋
 * @date 2018/4/25 20:22
 */
public class XmlMapper extends Mapper<LongWritable,Text,NullWritable,Text> {
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        context.write(NullWritable.get(),value);
    }
}
