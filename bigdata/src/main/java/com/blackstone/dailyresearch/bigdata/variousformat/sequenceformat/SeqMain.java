package com.blackstone.dailyresearch.bigdata.variousformat.sequenceformat;

import com.blackstone.dailyresearch.bigdata.Runner;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileAsTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;

/**
 * desc:
 *
 * @author 王彦锋
 * @date 2018/4/28 15:30
 */
public class SeqMain implements Runner {

    @Override
    public int run(String[] args) throws Exception {
        Configuration configuration = new Configuration();
        Job job = Job.getInstance(configuration, "SeqMain");
        job.setJarByClass(getClass());

        job.setMapperClass(SeqMapper.class);
        job.setReducerClass(SeqReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        //Sequence文件是自解释的，明确知道类型。
        //这里以其他程序生产的文件作为输入
        job.setInputFormatClass(SequenceFileAsTextInputFormat.class);
        //指定以Sequence方式输出
        job.setOutputFormatClass(SequenceFileOutputFormat.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        job.waitForCompletion(true);

        return 0;
    }


}
