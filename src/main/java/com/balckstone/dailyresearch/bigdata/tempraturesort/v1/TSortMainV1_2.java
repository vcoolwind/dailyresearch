package com.balckstone.dailyresearch.bigdata.tempraturesort.v1;

import com.balckstone.dailyresearch.bigdata.Runner;
import com.balckstone.dailyresearch.bigdata.util.FileSystemUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * @author vcoolwind
 */
public class TSortMainV1_2 implements Runner {
    @Override
    public int run(String[] args) throws Exception {
        Configuration configuration = new Configuration();
        FileSystemUtils.delete(configuration, args[1]);

        Job job = Job.getInstance(configuration, "TSortMainV1");
        job.setJarByClass(getClass());
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        //不强制指定为1，会生成多个Reduce文件。各自的文件中是排序的，两个文件合起来是穿插无须的。
        //job.setNumReduceTasks(1);
        job.setMapperClass(TSortMapperV1.class);
        job.setReducerClass(TSortReducerV1.class);
        job.setCombinerClass(TSortReducerV1.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        return job.waitForCompletion(true) ? 0 : 1;

    }
}
