package com.balckstone.dailyresearch.bigdata.variousformat.sequenceformat;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * desc:
 *
 * @author 王彦锋
 * @date 2018/4/28 17:01
 */
public class SeqMapper extends Mapper<Text, Text, Text, Text> {

    @Override
    protected void map(Text key, Text value, Context context) throws IOException, InterruptedException {
        context.write(key, value);
        context.getCounter("MyCounter","Mapper").increment(1);
    }
}
