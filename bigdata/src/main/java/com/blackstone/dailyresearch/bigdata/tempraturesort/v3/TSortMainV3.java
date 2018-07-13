package com.blackstone.dailyresearch.bigdata.tempraturesort.v3;

import static org.apache.hadoop.mapreduce.lib.input.KeyValueLineRecordReader.KEY_VALUE_SEPERATOR;

import com.blackstone.dailyresearch.bigdata.Runner;
import com.blackstone.dailyresearch.bigdata.util.FileSystemUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.partition.InputSampler;
import org.apache.hadoop.mapreduce.lib.partition.TotalOrderPartitioner;

/**
 * 手工指定分区，不采样。每个文件顺序下来，就是自然排序的。
 * @author vcoolwind
 */
public class TSortMainV3 implements Runner {
    @Override
    public int run(String[] args) throws Exception {
        Configuration configuration = new Configuration();
        FileSystemUtils.delete(configuration, args[1]);

        Job job = Job.getInstance(configuration, "TSortMainV3");
        job.setJarByClass(getClass());
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        //通过随机分区，让年份自然排序。
        job.setNumReduceTasks(2);
        job.setPartitionerClass(TotalOrderPartitioner.class);
        Path partitionOutputPath = new Path(args[2]);
        TotalOrderPartitioner.setPartitionFile(job.getConfiguration(), partitionOutputPath);

        job.setInputFormatClass(KeyValueTextInputFormat.class);
        job.getConfiguration().set(KEY_VALUE_SEPERATOR,",");

        job.setMapperClass(TSortMapperV3.class);
        job.setReducerClass(TSortReducerV3.class);
        job.setCombinerClass(TSortReducerV3.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        // InputSampler 从输入文件中随机筛选读取key，然后对K按默认排序，确定分区。---如果原始文件无法按key排序，则出现了问题。
        // 局限性蛮大的，key的比较器决定了排序方式。
        InputSampler.Sampler<Text, Text> sampler = new InputSampler.RandomSampler<>(0.01, 1000, 100);
        InputSampler.writePartitionFile(job, sampler);

        return job.waitForCompletion(true) ? 0 : 1;

    }
}
