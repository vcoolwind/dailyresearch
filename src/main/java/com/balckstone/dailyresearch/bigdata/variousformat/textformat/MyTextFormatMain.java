package com.balckstone.dailyresearch.bigdata.variousformat.textformat;

import com.balckstone.dailyresearch.bigdata.Runner;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class MyTextFormatMain implements Runner {
    @Override
    public int run(String[] args) throws Exception {
        Configuration configuration = new Configuration();
        Job job = Job.getInstance(configuration, "MyKVTextFormatMain");
        job.setJarByClass(getClass());

        //TextInputFormat使用LineRecordReader进行每行数据的处理
        //使用“textinputformat.record.delimiter”进行换行判断，不设置默认使用换行符。

        //测试数据 abc^def^gh
        job.getConfiguration().set("textinputformat.record.delimiter", "^");


        FileInputFormat.addInputPath(job,new Path(args[0]));
        FileOutputFormat.setOutputPath(job,new Path(args[1]));

        //TextInputFormat key为Long型，偏移位置，value为 每一行的位置
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
