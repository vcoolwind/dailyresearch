package com.blackstone.dailyresearch.custimport;

import com.blackstone.dailyresearch.designpatterns.template.completionservice.v5.CompletionServiceCallback;
import com.blackstone.dailyresearch.designpatterns.template.completionservice.v5.CompletionServiceTemplate;
import com.blackstone.dailyresearch.designpatterns.template.completionservice.v5.Runner;
import com.blackstone.dailyresearch.util.ConsoleLog;
import com.blackstone.dailyresearch.util.DataSourceUtil;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;

public class CustImporter {
    private static final String OTHER_SQL = "select count(1) from other_cust_summary where mobile= ? ";
    private static final String EXISTS_SQL = "select count(1) from other_cust_summary where mobile= ? and source_from = ?";
    private static final String CUSTINFO_SQL = "select count(1) from custinfo where mobileno= ? ";
    private static final String INSERT_SQL = "INSERT INTO other_cust_summary(mobile, tag1, source_from, isfirst)" +
            "VALUES(?,?,?,?);";

    public static void main(String[] args) throws Exception {
        CustImporter importer = new CustImporter();
        List<OtherCust> custs = new ArrayList<>();
        File f = new File("D:/du0815/m3.csv");
        List lines = FileUtils.readLines(f, "utf-8");
        for (Object line : lines) {
            String[] attrArray = line.toString().split(",");
            custs.add(new OtherCust(attrArray[0], attrArray[1], attrArray[2]));
        }

        ConsoleLog.println(custs.get(0));
        ConsoleLog.println(custs.size());


        //生成任务，提交线程池处理。
        CompletionServiceTemplate.getInstance().execute(10, custs.size(),
                new CompletionServiceCallback<Integer>() {
                    int i = 0;

                    @Override
                    public void handleTask() {
                        for (final OtherCust cust : custs) {
                            addTask(new Runner<Integer>() {
                                @Override
                                public Integer run() throws Exception {
                                    importer.doInsert(cust);
                                    return null;
                                }
                            });
                        }
                    }

                    @Override
                    public void handleResult(Integer result) {
                        ConsoleLog.println(i++);
                    }
                });
    }

    public boolean isFirst(Connection conn, String mobile) throws Exception {
        return isFirst(conn, mobile, OTHER_SQL) && isFirst(conn, mobile, CUSTINFO_SQL);
    }

    private boolean isFirst(Connection conn, String mobile, String sql) throws Exception {
        PreparedStatement statement = conn.prepareStatement(sql);
        statement.setString(1, mobile);
        ResultSet rs = statement.executeQuery();
        int count = 0;
        if (rs.next()) {
            count = rs.getInt(1);
        }
        rs.close();
        statement.close();
        if (count > 0) {
            return false;
        } else {
            return true;
        }
    }

    private boolean exists(Connection conn, OtherCust cust) throws Exception {
        PreparedStatement statement = conn.prepareStatement(EXISTS_SQL);
        statement.setString(1, cust.getMobile());
        statement.setString(2, cust.getSource_from());
        ResultSet rs = statement.executeQuery();
        int count = 0;
        if (rs.next()) {
            count = rs.getInt(1);
        }
        rs.close();
        statement.close();
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean doInsert(OtherCust cust) throws Exception {
        Connection conn = null;
        try {
            conn = DataSourceUtil.getConn();

            if (exists(conn, cust)) {
                ConsoleLog.println("exists:" + cust.getMobile() + " " + cust.getSource_from());
                return false;
            }

            String isFirst = isFirst(conn, cust.getMobile()) ? "1" : "0";
            cust.setIsFirst(isFirst);
            conn.setAutoCommit(false);
            PreparedStatement statement = conn.prepareStatement(INSERT_SQL);
            statement.setString(1, cust.getMobile());
            statement.setString(2, cust.getTag1());
            statement.setString(3, cust.getSource_from());
            statement.setString(4, cust.getIsFirst());
            boolean ret = statement.execute();
            statement.close();
            conn.commit();

            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            return  false;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

}
