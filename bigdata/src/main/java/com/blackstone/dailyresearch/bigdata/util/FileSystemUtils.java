package com.blackstone.dailyresearch.bigdata.util;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;

/**
 * @author vcoolwind
 */
public class FileSystemUtils {
    public static void copyFromLocal(Configuration conf, String localFile, String remoteDir) throws IOException {
        FileSystem fs = FileSystem.newInstance(conf);
        fs.copyFromLocalFile(new Path(localFile), new Path(remoteDir));
    }

    public static void makeDir(Configuration conf, String remoteDir) throws IOException {
        FileSystem fs = FileSystem.newInstance(conf);
        fs.mkdirs(new Path(remoteDir));
    }

    public static void list(Configuration conf, String remoteDir) throws IOException {
        FileSystem fs = FileSystem.newInstance(conf);
        //遍历文件
        RemoteIterator<LocatedFileStatus>  iterator=  fs.listFiles(new Path(remoteDir),true);
        while (iterator.hasNext()){
            LocatedFileStatus file =  iterator.next();
            String t1 = file.isDirectory()?".":" ";
            String t2 = file.getPath().getName();
            System.out.println(t1+"\t"+t2);
        }
    }

    public static void list2(Configuration conf, String remoteDir) throws IOException {
        FileSystem fs = FileSystem.newInstance(conf);
        FileStatus[] files =  fs.listStatus(new Path(remoteDir));
        for (FileStatus file:files) {
            String t1 = file.isDirectory()?".":" ";
            String t2 = file.getPath().getName();
            System.out.println(t1+"\t"+t2);
        }
    }



    public static void append(Configuration conf, String src, String dest) throws Exception {
        Path srcPath= new Path(src);
        FileSystem fs = FileSystem.get(srcPath.toUri(),conf);
        fs.append(new Path(dest));
    }

    public static void create(Configuration conf,String file) throws Exception {
        FileSystem fs = FileSystem.newInstance(conf);
        FSDataOutputStream out =  fs.create(new Path(file));
        out.writeChars("first line\n");
        out.writeChars("second line\n");
        out.close();
    }

    public static void delete(Configuration conf,String file) throws Exception {
        Path path = new Path(file);
        FileSystem fs = FileSystem.newInstance(conf);
        if(fs.exists(path)){
            System.out.println("file exist:"+file);
            fs.delete(path,true);
        }else{
            System.out.println("file not exist:"+file);
        }
    }


    public static void main(String[] args) throws Exception {
        String funcName = args[0];
        Configuration configuration = new Configuration();
        if ("copyFromLocal".equals(funcName)) {
            copyFromLocal(configuration, args[1], args[2]);
        } else if ("makeDir".equals(funcName)) {
            makeDir(configuration, args[1]);
        }else if ("append".equals(funcName)) {
            append(configuration,args[1], args[2]);
        }else if ("create".equals(funcName)) {
            create(configuration,args[1]);
        }else if ("list".equals(funcName)) {
            list(configuration,args[1]);
        } else if ("list2".equals(funcName)) {
            list2(configuration,args[1]);
        }else if ("delete".equals(funcName)) {
            list2(configuration,args[1]);
        }  else {
            System.out.println("invalid paras");
        }
    }
}
