package com.blackstone.dailyresearch.bigdata.wordcountsort2;

import java.io.IOException;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;


/**
 * @author vcoolwind
 */
public class WordCountSortMapper2 extends Mapper<Text, Text,IntWritable,Text> {
    @Override
    protected void map(Text key, Text value, Context context) throws IOException, InterruptedException {
        context.write(new IntWritable(Integer.parseInt(value.toString())),key);
    }
}
