package com.balckstone.dailyresearch.bigdata.tempraturesort.v4;

import com.balckstone.dailyresearch.bigdata.Runner;
import com.balckstone.dailyresearch.bigdata.io.IntPair;
import com.balckstone.dailyresearch.bigdata.util.FileSystemUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 *  辅助排序
 *
 * @author vcoolwind
 */
public class TSortMainV4 implements Runner {
    @Override
    public int run(String[] args) throws Exception {
        Configuration configuration = new Configuration();
        FileSystemUtils.delete(configuration, args[1]);

        Job job = Job.getInstance(configuration, "TSortMainV4");
        job.setJarByClass(getClass());
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        job.setNumReduceTasks(2);

        job.setMapperClass(TSortMapperV4.class);
        job.setReducerClass(TSortReducerV4.class);
        job.setCombinerClass(TSortReducerV4.class);

        job.setOutputKeyClass(IntPair.class);
        job.setOutputValueClass(NullWritable.class);


        job.setPartitionerClass(FirstPartitioner2.class);
        // 优先按key排序，然后按value排序
        job.setSortComparatorClass(KeyComparator.class);

        // 对可以进行分组，取第一个。
        job.setGroupingComparatorClass(GroupComparator.class);

        return job.waitForCompletion(true) ? 0 : 1;

    }


    public static class FirstPartitioner1
            extends Partitioner<IntPair, NullWritable> {

        @Override
        public int getPartition(IntPair key, NullWritable value, int numPartitions) {
            // multiply by 127 to perform some mixing
            //仅仅保证每年的数据在一个分区中
            return Math.abs(key.getFirst() * 127) % numPartitions;
        }
    }
    public static class FirstPartitioner2
            extends Partitioner<IntPair, NullWritable> {

        @Override
        public int getPartition(IntPair key, NullWritable value, int numPartitions) {
            //做简单分区，人工指定，不管key的分布情况。可能会造成Reducer负载有很大的差异。
            if (key.getFirst() >= 2000) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    public static class KeyComparator extends WritableComparator {
        protected KeyComparator() {
            super(IntPair.class, true);
        }

        @Override
        public int compare(WritableComparable w1, WritableComparable w2) {
            IntPair ip1 = (IntPair) w1;
            IntPair ip2 = (IntPair) w2;
            int cmp = IntPair.compare(ip1.getFirst(), ip2.getFirst());
            if (cmp != 0) {
                return cmp;
            }
            return -IntPair.compare(ip1.getSecond(), ip2.getSecond()); //reverse
        }
    }

    public static class GroupComparator extends WritableComparator {
        protected GroupComparator() {
            super(IntPair.class, true);
        }

        @Override
        public int compare(WritableComparable w1, WritableComparable w2) {
            IntPair ip1 = (IntPair) w1;
            IntPair ip2 = (IntPair) w2;
            return IntPair.compare(ip1.getFirst(), ip2.getFirst());
        }
    }

}
