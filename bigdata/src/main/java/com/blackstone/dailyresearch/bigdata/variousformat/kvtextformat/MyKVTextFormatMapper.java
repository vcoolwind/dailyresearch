package com.blackstone.dailyresearch.bigdata.variousformat.kvtextformat;

import java.io.IOException;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * desc:
 * 作为Mapper，是不知道输入源的。InputFormat已经转化为对应的类型了。
 *
 * @author 王彦锋
 * @date 2018/4/26 16:39
 */
public class MyKVTextFormatMapper extends Mapper<Text, Text, Text, Text> {
    @Override
    protected void map(Text key, Text value, Context context) throws IOException, InterruptedException {
        //这里做简单的原样输出
        context.write(key, value);
    }
}
