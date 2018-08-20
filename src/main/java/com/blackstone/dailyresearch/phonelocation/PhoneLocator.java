package com.blackstone.dailyresearch.phonelocation;

import com.blackstone.dailyresearch.common.KVEntry;
import com.blackstone.dailyresearch.designpatterns.template.completionservice.v5.CompletionServiceCallback;
import com.blackstone.dailyresearch.designpatterns.template.completionservice.v5.CompletionServiceTemplate;
import com.blackstone.dailyresearch.designpatterns.template.completionservice.v5.Runner;
import com.blackstone.dailyresearch.helper.HttpClientHelper;
import com.blackstone.dailyresearch.util.CloseUtils;
import com.blackstone.dailyresearch.util.ConsoleLog;
import com.blackstone.dailyresearch.util.DataSourceUtil;
import com.blackstone.dailyresearch.util.StringHelper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import us.codecraft.webmagic.selector.Html;

public class PhoneLocator {
    public static final String GET_URL = "http://www.ip138.com:8080/search.asp?mobile=%s&action=mobile";

    public static final String NEW_SEGMENT_SQL1 = "select distinct phone_segment from (" +
            "  select  substring(mobileno,1,7) as phone_segment  " +
            "  from custinfo " +
            "  where  not exists (select 1 from phonenum_location where num_segment = substring(mobileno,1,7)) " +
            "  limit 300 " +
            ") t  ";

    public static final String NEW_SEGMENT_SQL2 = "select distinct phone_segment from (" +
            "  select  substring(mobile,1,7) as phone_segment  " +
            "  from other_cust_summary " +
            "  where  not exists (select 1 from phonenum_location where num_segment = substring(mobile,1,7)) " +
            "  limit 300 " +
            ") t  ";

    public static final String INSERT_SQL = "INSERT INTO phonenum_location(num_segment, location) VALUES(?,?)";

    public static void main(String[] args) throws Exception {
        System.out.println(getLocation("1868226"));
        iterateLocation();
    }

    private static void iterateLocation() throws Exception {
        while (true) {
            List<String> newSegments = queryNewSegments();
            if (newSegments.size() < 10) {
                ConsoleLog.println("提取数量太少，终止:" + newSegments.size());
                break;
            } else {
                ConsoleLog.println("新一轮提取：" + newSegments.size());
            }
            //生成任务，提交线程池处理。
            CompletionServiceTemplate.getInstance().execute(10, newSegments.size(),
                    new CompletionServiceCallback<KVEntry<String, String>>() {

                        @Override
                        public void handleTask() {
                            for (final String phoneSegment : newSegments) {
                                addTask(new Runner<KVEntry<String, String>>() {
                                    @Override
                                    public KVEntry<String, String> run() {
                                        return new KVEntry<>(phoneSegment, getLocation(phoneSegment));
                                    }
                                });
                            }
                        }

                        @Override
                        public void handleResult(KVEntry<String, String> result) {
                            ConsoleLog.println(result.toString());
                            insertNewSegment(result.getKey(), result.getValue());
                        }
                    });
        }
    }

    public static String getLocation(String phoneNum) {
        try {
            String url = String.format(GET_URL, phoneNum);
            //System.out.println(url);
            String data = HttpClientHelper.getData(url, "GBK");
            //System.out.println(data);
            Html html = new Html(data);
            //String location = html.$("body > table:nth-child(6) > tbody > tr:nth-child(3) > td.tdc2").toString();
            String location = html.xpath("/html/body/table[2]/tbody/tr[3]/td[2]/text()").toString();
            return location;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean insertNewSegment(String mobileSegment, String location) {
        if (StringUtils.isBlank(mobileSegment) || StringUtils.isBlank(location) || location.length() > 32) {
            return false;
        }
        Connection conn = null;
        PreparedStatement statement = null;
        try {
            conn = DataSourceUtil.getConn();
            conn.setAutoCommit(false);
            statement = conn.prepareStatement(INSERT_SQL);
            statement.setString(1, mobileSegment);
            statement.setString(2, location);

            boolean ret = statement.execute();
            statement.close();
            conn.commit();
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            CloseUtils.close(statement);
            CloseUtils.close(conn);
        }
    }

    public static List<String> queryNewSegments() {
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        List<String> ret = new LinkedList<>();
        try {
            conn = DataSourceUtil.getConn();
            statement = conn.prepareStatement(NEW_SEGMENT_SQL1);
            rs = statement.executeQuery();
            while (rs.next()) {
                String phoneNum = rs.getString(1);
                String prefix = StringUtils.substring(phoneNum, 0, 2);
                if (phoneNum != null && phoneNum.length() == 7 && StringHelper.isIn(prefix, "13", "14", "15", "16", "17", "18", "19")) {
                    ret.add(phoneNum);
                }
            }
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            return ret;
        } finally {
            CloseUtils.close(rs);
            CloseUtils.close(statement);
            CloseUtils.close(conn);
        }
    }
}
