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
public class TSortMainV1 implements Runner {
    @Override
    public int run(String[] args) throws Exception {
        Configuration configuration = new Configuration();
        FileSystemUtils.delete(configuration, args[1]);

        Job job = Job.getInstance(configuration, "TSortMainV1");
        job.setJarByClass(getClass());
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        //这里明确使用一个Reducer，确保输入排序后去的最大值。但效率会存在问题，需要用更好的方案。
        job.setNumReduceTasks(1);
        job.setMapperClass(TSortMapperV1.class);
        job.setReducerClass(TSortReducerV1.class);
        job.setCombinerClass(TSortReducerV1.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        return job.waitForCompletion(true) ? 0 : 1;

    }
}
