package com.blackstone.dailyresearch.bigdata.hdfsfile;

import com.blackstone.dailyresearch.bigdata.Runner;
import java.io.IOException;
import java.util.Arrays;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;

/**
 * desc:
 * 针对大量小文件，可以使用SequenceFile，key为文件名，value为文件内容。这样可以避免小文件占用问题。
 *
 * @author 王彦锋
 * @date 2018/4/16 16:25
 */
public class SeqFileTest implements Runner {
    public static void main(String[] args) throws Exception {
        int ret = new SeqFileTest().run(args);
        System.exit(ret);
    }

    @Override
    public int run(String[] args) throws Exception {
        System.out.println(this.getClass().getName() + " -- " + Arrays.toString(args));
        String op = args[0];
        String file = args[1];
        if ("write".equals(op)) {
            write(file);
        } else if ("read".equals(op)) {
            read(file);
        } else if ("writeBlockCompress".equals(op)) {
            writeBlockCompress(file);
        } else if ("writeRecordCompress".equals(op)) {
            writeRecordCompress(file);
        } else {
            System.out.println("op error");
            return -1;
        }

        return 0;
    }

    private void write(String file) throws IOException {
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);
        Path seqFile = new Path(file);
        SequenceFile.Writer writer = SequenceFile.createWriter(conf, SequenceFile.Writer.file(seqFile),
                SequenceFile.Writer.keyClass(Text.class),
                SequenceFile.Writer.valueClass(Text.class),
                SequenceFile.Writer.compression(SequenceFile.CompressionType.NONE));


        for (int i = 0; i < 10; i++) {
            writer.append(new Text("line" + i), new Text("value" + i));
        }
        writer.close();
    }

    private void writeBlockCompress(String file) throws IOException {
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);
        Path seqFile = new Path(file);
        SequenceFile.Writer writer = SequenceFile.createWriter(conf, SequenceFile.Writer.file(seqFile),
                SequenceFile.Writer.keyClass(Text.class),
                SequenceFile.Writer.valueClass(Text.class),
                SequenceFile.Writer.compression(SequenceFile.CompressionType.BLOCK));

        for (int i = 0; i < 10; i++) {
            writer.append(new Text("line" + i), new Text("value" + i));
        }
        writer.close();
    }

    private void writeRecordCompress(String file) throws IOException {
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);
        Path seqFile = new Path(file);
        SequenceFile.Writer writer = SequenceFile.createWriter(conf, SequenceFile.Writer.file(seqFile),
                SequenceFile.Writer.keyClass(Text.class),
                SequenceFile.Writer.valueClass(Text.class),
                SequenceFile.Writer.compression(SequenceFile.CompressionType.RECORD));

        for (int i = 0; i < 10; i++) {
            writer.append(new Text("line" + i), new Text("value" + i));
        }
        writer.close();
    }


    private void read(String file) throws IOException {
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);
        Path seqFile = new Path(file);
        SequenceFile.Reader reader = new SequenceFile.Reader(conf, SequenceFile.Reader.file(seqFile));
        System.out.println("Block Compresssed:" + reader.isBlockCompressed());
        System.out.println("Compresssed:" + reader.isCompressed());
        System.out.println("CompresssType:" + reader.getCompressionType());
        Text key = new Text();
        Text value = new Text();
        while (reader.next(key, value)) {
            System.out.println(key.toString() + "\t" + value.toString());
        }
        reader.close();
    }

}
