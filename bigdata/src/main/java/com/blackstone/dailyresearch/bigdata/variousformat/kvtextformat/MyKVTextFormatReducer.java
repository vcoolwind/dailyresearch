package com.blackstone.dailyresearch.bigdata.variousformat.kvtextformat;

import java.io.IOException;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * desc:
 * reducer的输出类型，也不是由reducer决定的，他只负责输出结果。
 * 结果是否被压缩，是否被输出类编码，它是不知晓的。
 *
 * @author 王彦锋
 * @date 2018/4/26 17:21
 */
public class MyKVTextFormatReducer extends Reducer<Text,Text,NullWritable,Text> {
    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        for (Text value : values) {
            context.write(NullWritable.get(),new Text(key+" = "+value));
        }
    }
}
