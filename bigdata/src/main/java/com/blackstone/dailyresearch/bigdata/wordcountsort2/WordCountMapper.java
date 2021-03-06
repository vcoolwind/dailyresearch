package com.blackstone.dailyresearch.bigdata.wordcountsort2;

import java.io.IOException;
import java.util.StringTokenizer;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * @author vcoolwind
 */
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
