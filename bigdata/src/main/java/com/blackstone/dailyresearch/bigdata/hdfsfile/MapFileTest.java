package com.blackstone.dailyresearch.bigdata.hdfsfile;

import com.blackstone.dailyresearch.bigdata.Runner;
import java.io.IOException;
import java.util.Arrays;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.MapFile;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;

/**
 * desc:
 * 针对大量小文件，可以使用SequenceFile，key为文件名，value为文件内容。这样可以避免小文件占用问题。
 *
 * @author 王彦锋
 * @date 2018/4/16 16:25
 */
public class MapFileTest implements Runner {
    public static void main(String[] args) throws Exception {
        int ret = new MapFileTest().run(args);
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
            int len = args.length - 2;
            String[] keys = new String[len];
            System.out.println("--->"+len);
            for (int i = 2; i < args.length; i++) {
                System.out.println("--->"+args[i]);
                keys[i - 2] = args[i];
            }
            read(file, keys);
        } else if ("read100".equals(op)) {
            read100(file);
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
        Path mapDir = new Path(file);

        MapFile.Writer writer = new MapFile.Writer(conf, mapDir,
                MapFile.Writer.keyClass(Text.class),
                MapFile.Writer.valueClass(Text.class)
                //,MapFile.Writer.compression(SequenceFile.CompressionType.NONE)
        );
        for (int i = 0; i < 100000; i++) {

            writer.append(new Text("line" + StringUtils.leftPad("" + i, 5, "0")), new Text("value" + i));
        }
        writer.close();
    }

    private void writeBlockCompress(String file) throws IOException {
        Configuration conf = new Configuration();
        Path mapDir = new Path(file);

        MapFile.Writer writer = new MapFile.Writer(conf, mapDir,
                MapFile.Writer.keyClass(Text.class),
                MapFile.Writer.valueClass(Text.class),
                MapFile.Writer.compression(SequenceFile.CompressionType.BLOCK)
        );

        for (int i = 0; i < 10; i++) {
            writer.append(new Text("line" + i), new Text("value" + i));
        }
        writer.close();
    }

    private void writeRecordCompress(String file) throws IOException {
        Configuration conf = new Configuration();
        Path mapDir = new Path(file);

        MapFile.Writer writer = new MapFile.Writer(conf, mapDir,
                MapFile.Writer.keyClass(Text.class),
                MapFile.Writer.valueClass(Text.class),
                MapFile.Writer.compression(SequenceFile.CompressionType.RECORD)
        );

        for (int i = 0; i < 10; i++) {
            writer.append(new Text("line" + i), new Text("value" + i));
        }
        writer.close();
    }


    private void read(String file, String[] keys) throws IOException {
        Configuration conf = new Configuration();
        Path mapDir = new Path(file);

        MapFile.Reader reader = new MapFile.Reader(mapDir, conf);

        for (String key : keys) {
            Text value = new Text();
            reader.get(new Text(key), value);
            System.out.println(key+"\t"+value.toString());
        }

        reader.close();
    }

    private void read100(String file) throws IOException {
        Configuration conf = new Configuration();
        Path mapDir = new Path(file);

        MapFile.Reader reader = new MapFile.Reader(mapDir, conf);
        Text key = new Text();
        Text value = new Text();
        int i=0;
        while (reader.next(key, value)) {
            System.out.println(key.toString() + "\t" + value.toString());
            i++;
            if(i>100){
                break;
            }
        }
        reader.close();
    }

}
