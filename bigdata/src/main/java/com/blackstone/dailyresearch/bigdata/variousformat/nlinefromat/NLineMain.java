package com.blackstone.dailyresearch.bigdata.variousformat.nlinefromat;

import com.blackstone.dailyresearch.bigdata.Runner;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.NLineInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * desc:
 *
 * @author 王彦锋
 * @date 2018/4/28 15:30
 */
public class NLineMain implements Runner {

    @Override
    public int run(String[] args) throws Exception {
        Configuration configuration = new Configuration();
        Job job = Job.getInstance(configuration, "SeqMain");
        job.setJarByClass(getClass());

        job.setMapperClass(NLineMapper.class);
        job.setReducerClass(NLineReducer.class);

        job.setMapOutputKeyClass(NullWritable.class);
        job.setMapOutputValueClass(Text.class);

        job.setOutputKeyClass(NullWritable.class);
        job.setOutputValueClass(Text.class);

        job.setNumReduceTasks(4);
        job.setPartitionerClass(PartitionerByIndex.class);

        job.setInputFormatClass(NLineInputFormat.class);
        NLineInputFormat.setNumLinesPerSplit(job, 5);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        job.waitForCompletion(true);

        return 0;
    }

    private static class PartitionerByIndex extends Partitioner<NullWritable, Text> {

        @Override
        public int getPartition(NullWritable key, Text value, int numPartitions) {
            int index = Integer.parseInt(value.toString().split("\t")[1]);
            return Math.abs(index % numPartitions);
        }
    }

}
