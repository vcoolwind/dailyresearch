package com.balckstone.dailyresearch.bigdata.wordcountsort2;

import com.stone.bigdata.util.DecreasingComparator;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


public class WordCountSort2Main {
    public static void main(String[] args) throws Exception {
        Configuration configuration = new Configuration();
        try {
            FileSystem.get(configuration).delete(new Path("wordcount-temp-output"), true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Path tempDir = new Path("wordcount-temp-output");


        Job job = Job.getInstance(configuration, "word count");
        job.setJarByClass(WordCountSort2Main.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, tempDir);

        job.setMapperClass(WordCountMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        job.setCombinerClass(WordCountReducer.class);

        job.setReducerClass(WordCountReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        job.waitForCompletion(true);

        Job sortJob = Job.getInstance(configuration, "sort");
        sortJob.setJarByClass(WordCountSort2Main.class);
        FileInputFormat.addInputPath(sortJob, tempDir);
        FileOutputFormat.setOutputPath(sortJob, new Path(args[1]));
        sortJob.setSortComparatorClass(DecreasingComparator.IntDecreasingComparator.class);
        sortJob.setNumReduceTasks(1);
        sortJob.setInputFormatClass(KeyValueTextInputFormat.class);

        sortJob.setMapperClass(WordCountSortMapper2.class);
        sortJob.setMapOutputKeyClass(IntWritable.class);
        sortJob.setMapOutputValueClass(Text.class);

        sortJob.setReducerClass(WordCountSort2Reducer.class);
        sortJob.setOutputKeyClass(Text.class);
        sortJob.setOutputValueClass(IntWritable.class);

        sortJob.waitForCompletion(true);
        System.exit(0);

    }
}
