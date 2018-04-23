package com.balckstone.dailyresearch.bigdata.wordcount;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.junit.Test;

public class WordCountMapperTest {
    @Test
    public void testMap() throws IOException {
        String line = "abc def abc";
        new MapDriver<Object, Text, Text, IntWritable>()
                .withMapper(new WordCountMapper())
                .withInput(new LongWritable(0), new Text(line))
                .withOutput(new Text("abc"), new IntWritable(1))
                .withOutput(new Text("def"), new IntWritable(1))
                .withOutput(new Text("abc"), new IntWritable(1))
                .runTest();
    }

}
