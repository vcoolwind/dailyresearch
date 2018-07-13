package com.blackstone.dailyresearch.bigdata.variousformat.nlinefromat;

import java.io.IOException;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class NLineReducer extends Reducer<NullWritable, Text, NullWritable, Text> {

    // https://blog.csdn.net/doegoo/article/details/50969028
    // https://blog.csdn.net/doegoo/article/details/50392109
    @Override
    protected void reduce(NullWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        for (Text value : values) {
            context.write(NullWritable.get(), value);
        }
    }
}
