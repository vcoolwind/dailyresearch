package com.balckstone.dailyresearch.bigdata.tempraturesort.v2;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

/**
 * desc:
 *
 * @author 王彦锋
 * @date 2018/5/7 16:30
 */
public class MyYearPartitioner extends Partitioner<Text, IntWritable> {

    @Override
    public int getPartition(Text text, IntWritable intWritable, int numPartitions) {
        int year = Integer.parseInt(text.toString());
        //做简单分区，人工指定，不管key的分布情况。可能会造成Reducer负载有很大的差异。
        if (year >= 2000) {
            return 1;
        } else {
            return 0;
        }
    }
}
