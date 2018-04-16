package com.balckstone.dailyresearch.bigdata.wordcountsort;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.StringTokenizer;

public class WordCountMapper extends Mapper<Object, Text, Text, IntWritable> {
    @Override
    protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        StringTokenizer st = new StringTokenizer(value.toString());
        while (st.hasMoreTokens()) {
            String word = st.nextToken();
            context.write(new Text(word), new IntWritable(1));
        }
    }
}
