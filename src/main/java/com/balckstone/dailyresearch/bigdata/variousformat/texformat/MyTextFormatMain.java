package com.balckstone.dailyresearch.bigdata.variousformat.texformat;

import com.balckstone.dailyresearch.bigdata.Runner;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class MyTextFormatMain implements Runner {
    @Override
    public int run(String[] args) throws Exception {
        Configuration configuration = new Configuration();
        Job job = Job.getInstance(configuration,"MyTextFormatMain");

        //TextInputFormat使用LineRecordReader进行每行数据的处理
        //使用“textinputformat.record.delimiter”进行换行判断。


        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        job.setMapperClass(MyTextFormatMapper.class);
        job.setReducerClass(MyTextFormatReducer.class);

        job.setMapOutputKeyClass(NullWritable.class);
        job.setMapOutputValueClass(Text.class);

        job.setOutputKeyClass(NullWritable.class);
        job.setOutputValueClass(Text.class);

        job.setNumReduceTasks(1);

        job.waitForCompletion(true);

        return 0;
    }
}
