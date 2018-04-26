package com.balckstone.dailyresearch.bigdata.variousformat.xmlformat;

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
        //拿到行数据就输出，实际应用中，肯定是对行数据value进行分解的。
        context.write(NullWritable.get(),value);
    }
}
