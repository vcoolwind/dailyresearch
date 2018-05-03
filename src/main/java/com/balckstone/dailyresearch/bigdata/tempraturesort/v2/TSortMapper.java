package com.balckstone.dailyresearch.bigdata.tempraturesort.v2;

import com.balckstone.dailyresearch.bigdata.io.IntPair;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @author vcoolwind
 */
public class TSortMapper extends Mapper<LongWritable, Text, IntPair, IntWritable> {
    private static final Logger log = LoggerFactory.getLogger(TSortMapper.class);

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        Object[] values = getYearMonthAndTemperature(value);
        if (values != null) {
            context.write(new Text(values[0].toString()), new IntWritable((Integer) values[1]));
        }
    }

    private Object[] getYearMonthAndTemperature(Text value) {
        if (value == null || value.toString().trim().length() == 0) {
            return null;
        }
        String[] values = value.toString().split(",");
        if (values.length < 4) {
            return null;
        }
        String ymd = values[0];
        String temperature = values[3];
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy");
        try {
            String outYM = sdf2.format(sdf1.parse(ymd));
            Integer outTemperature = Integer.parseInt(temperature);
            return new Object[]{outYM, outTemperature};
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
