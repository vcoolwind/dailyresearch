package com.blackstone.dailyresearch.bigdata.tempraturesort.v2;

import com.blackstone.dailyresearch.bigdata.Runner;
import com.blackstone.dailyresearch.bigdata.util.FileSystemUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * 手工指定分区，不采样。每个文件顺序下来，就是自然排序的。
 * @author vcoolwind
 */
public class TSortMainV2 implements Runner {
    @Override
    public int run(String[] args) throws Exception {
        Configuration configuration = new Configuration();
        FileSystemUtils.delete(configuration, args[1]);

        Job job = Job.getInstance(configuration, "TSortMainV2");
        job.setJarByClass(getClass());
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        //通过分区，让年份自然排序。
        job.setNumReduceTasks(2);
        job.setPartitionerClass(MyYearPartitioner.class);
        job.setMapperClass(TSortMapperV2.class);
        job.setReducerClass(TSortReducerV2.class);
        job.setCombinerClass(TSortReducerV2.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        return job.waitForCompletion(true) ? 0 : 1;

    }
}
