package com.blackstone.dailyresearch.bigdata.tempraturesort.v4;

import com.blackstone.dailyresearch.bigdata.io.IntPair;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author vcoolwind
 */
public class TSortMapperV4 extends Mapper<LongWritable, Text, IntPair, NullWritable> {
    private static final Logger log = LoggerFactory.getLogger(TSortMapperV4.class);

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        IntPair valuePair = getYearMonthAndTemperature(value);
        if (valuePair != null) {
            context.write(valuePair,NullWritable.get());
        }else{
            context.getCounter("SortMapper","InvalidLine").increment(1);
        }
    }

    private IntPair getYearMonthAndTemperature(Text value) {
        if (value == null || value.toString().trim().length() == 0) {
            return null;
        }
        String[] values = value.toString().split(",");
        if (values.length < 3) {
            return null;
        }
        String ymd = values[0];
        String temperature = values[2];
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy");
        try {
            Integer outYM = Integer.parseInt(sdf2.format(sdf1.parse(ymd)));
            Integer outTemperature = Integer.parseInt(temperature);
            return new IntPair(outYM, outTemperature);
        } catch (Exception e) {
            log.warn("invalid value:" + value, e);
        }
        return null;
    }


    public static void main(String[] args) throws ParseException {
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM");
        Date dt = sdf1.parse("2001/1/1");
        System.out.println(sdf2.format(dt));
    }

}
