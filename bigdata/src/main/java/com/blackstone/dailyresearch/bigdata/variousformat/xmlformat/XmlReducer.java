package com.blackstone.dailyresearch.bigdata.variousformat.xmlformat;

import java.io.IOException;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * desc:
 *
 * @author 王彦锋
 * @date 2018/4/26 16:27
 */
public class XmlReducer extends Reducer<NullWritable, Text, NullWritable, Text> {
    @Override
    protected void reduce(NullWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        //这里不做业务处理，仅仅对行数据进行输出
        for (Text value : values) {
            context.write(NullWritable.get(), value);
        }
    }
}
