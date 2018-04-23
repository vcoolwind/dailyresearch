package com.balckstone.dailyresearch.bigdata.wordcount;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mrunit.mapreduce.ReduceDriver;
import org.junit.Test;

public class WordCountReducerTest {

    @Test
    public void reduce() throws IOException {
        new ReduceDriver<Text, IntWritable, Text, IntWritable>()
                .withReducer(new WordCountReducer())
                .withInput(new Text("abc"),Arrays.asList(new IntWritable(1),new IntWritable(2)))
                .withOutput(new Text("abc"),new IntWritable(3))
                .runTest();
    }
}