package com.balckstone.dailyresearch.bigdata.variousformat.sequenceformat;

import java.io.IOException;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class SeqReducer extends Reducer<Text, Text, Text, Text> {

    // https://blog.csdn.net/doegoo/article/details/50969028
    // https://blog.csdn.net/doegoo/article/details/50392109
    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        for (Text value : values) {
            context.write(key, value);
        }
        //添加自定义的Counter
        context.getCounter("MyCounter","Reducer").increment(1);

    }
}
