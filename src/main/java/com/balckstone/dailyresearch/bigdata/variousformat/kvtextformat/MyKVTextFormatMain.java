package com.balckstone.dailyresearch.bigdata.variousformat.kvtextformat;

import com.balckstone.dailyresearch.bigdata.Runner;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import static org.apache.hadoop.mapreduce.lib.input.KeyValueLineRecordReader.KEY_VALUE_SEPERATOR;

public class MyKVTextFormatMain implements Runner {
    @Override
    public int run(String[] args) throws Exception {
        Configuration configuration = new Configuration();
        Job job = Job.getInstance(configuration, "MyKVTextFormatMain");
        job.setJarByClass(getClass());

        //测试数据 k1,abc\nk2,def\nk3,gh
        //默认是 \t
        job.getConfiguration().set(KEY_VALUE_SEPERATOR,",");

        FileInputFormat.addInputPath(job,new Path(args[0]));
        FileOutputFormat.setOutputPath(job,new Path(args[1]));

        //KeyValueTextInputFormat key为第一个KEY_VALUE_SEPERATOR前的字符，value为本行后面数据
        job.setInputFormatClass(KeyValueTextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        job.setMapperClass(MyKVTextFormatMapper.class);
        job.setReducerClass(MyKVTextFormatReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);

        job.setOutputKeyClass(NullWritable.class);
        job.setOutputValueClass(Text.class);
        job.setNumReduceTasks(1);
        job.waitForCompletion(true);

        return 0;
    }
}
