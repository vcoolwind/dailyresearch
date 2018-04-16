package com.balckstone.dailyresearch.bigdata.wordcountsort;

import com.balckstone.dailyresearch.bigdata.Runner;
import com.balckstone.dailyresearch.bigdata.util.DecreasingComparator;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.map.InverseMapper;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;

/**
 * @author vcoolwind
 */
public class WordCountSortMain implements Runner {
    public static void main(String[] args) throws Exception {
        new WordCountSortMain().run(args);
    }

    @Override
    public int run(String[] args) throws Exception {

        Configuration configuration = new Configuration();

        Path tempDir = new Path("wordcount-temp-output");
        try {
            Job job = Job.getInstance(configuration, "word count");
            job.setJarByClass(WordCountSortMain.class);
            FileInputFormat.addInputPath(job, new Path(args[0]));
            FileOutputFormat.setOutputPath(job, tempDir);

            job.setMapperClass(WordCountMapper.class);
            job.setMapOutputKeyClass(Text.class);
            job.setMapOutputValueClass(IntWritable.class);

            job.setCombinerClass(WordCountReducer.class);

            job.setReducerClass(WordCountReducer.class);
            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(IntWritable.class);
            //OutPutFormat类，该类负责输出结果的格式。可以通过job.setOutputFormatClass()方法进行设置。
            // 默认使用TextOUtputFormat类，得到<Key,value>对。

            //指定输出SequenceFile，减少碎文件。
            job.setOutputFormatClass(SequenceFileOutputFormat.class);
            job.waitForCompletion(true);


            Job sortJob = Job.getInstance(configuration, "sort");
            sortJob.setJarByClass(WordCountSortMain.class);
            FileInputFormat.addInputPath(sortJob, tempDir);
            FileOutputFormat.setOutputPath(sortJob, new Path(args[1]));

            //TextInputFormat<LongWritable,Text>这个是默认的数据格式类。
            // key代表当前行数据距离文件开始的距离，value代码当前行字符串。
            //这里使用SequenceFile，是利用在上面输出的是SequenceFile。
            sortJob.setInputFormatClass(SequenceFileInputFormat.class);
            sortJob.setMapperClass(InverseMapper.class);
            sortJob.setNumReduceTasks(1);
            sortJob.setSortComparatorClass(DecreasingComparator.IntDecreasingComparator.class);

            sortJob.setOutputKeyClass(IntWritable.class);
            sortJob.setOutputValueClass(Text.class);
            sortJob.waitForCompletion(true);
        } finally {
            FileSystem.get(configuration).delete(tempDir, true);
        }
        return 0;
    }

    private static class IntWritableDecreasingComparator extends IntWritable.Comparator {
        @Override
        public int compare(WritableComparable a, WritableComparable b) {
            return -super.compare(a, b);
        }

        @Override
        public int compare(byte[] b1, int s1, int l1, byte[] b2, int s2, int l2) {
            return -super.compare(b1, s1, l1, b2, s2, l2);
        }
    }
}
