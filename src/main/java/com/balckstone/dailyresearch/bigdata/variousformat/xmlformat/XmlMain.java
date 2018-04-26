package com.balckstone.dailyresearch.bigdata.variousformat.xmlformat;

import com.balckstone.dailyresearch.bigdata.Runner;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * desc:
 *
 * @author 王彦锋
 * @date 2018/4/25 21:26
 */
public class XmlMain implements Runner {
    @Override
    public int run(String[] args) throws Exception {
        Configuration config = new Configuration();
        config.set(XmlInputFormat.START_TAG_KEY, "<row");
        config.set(XmlInputFormat.END_TAG_KEY, "/>");
        Job job = Job.getInstance(config,"XmlMain");
        job.setJarByClass(getClass());
        FileInputFormat.addInputPath(job,new Path(args[0]));
        FileOutputFormat.setOutputPath(job,new Path(args[1]));
        job.setMapperClass(XmlMapper.class);
        job.setMapOutputKeyClass(NullWritable.class);
        job.setMapOutputValueClass(Text.class);

        job.setReducerClass(XmlReducer.class);
        job.setOutputKeyClass(NullWritable.class);
        job.setOutputValueClass(Text.class);
        job.setNumReduceTasks(1);
        job.setInputFormatClass(XmlInputFormat.class);
        job.waitForCompletion(true);
        return 0;
    }


}
