package com.blackstone.dailyresearch.jdbc;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;

public class JDBCHelper {
    private static final String OTHER_SQL = "select count(1) from other_cust_summary where mobile= ? ";
    private static final String EXISTS_SQL = "select count(1) from other_cust_summary where mobile= ? and source_from = ?";
    private static final String CUSTINFO_SQL = "select count(1) from custinfo where mobileno= ? ";
    private static final String INSERT_SQL = "INSERT INTO other_cust_summary(mobile, tag1, source_from, isfirst)" +
            "VALUES(?,?,?,?);";
    private String url;
    private String user;
    private String password;
    private Connection conn;

    public JDBCHelper(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }



    private synchronized Connection getConn() throws Exception {
        if (conn == null || conn.isClosed()) {
            Class.forName("org.postgresql.Driver").newInstance();
            conn = DriverManager.getConnection(url, user, password);
        }
        return conn;
    }

    public boolean isFirst(String mobile) throws Exception {
        return isFirst(mobile, OTHER_SQL) && isFirst(mobile, CUSTINFO_SQL);
    }

    private boolean isFirst(String mobile, String sql) throws Exception {
        PreparedStatement statement = getConn().prepareStatement(sql);
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

    private boolean exists(OtherCust cust) throws Exception {
        PreparedStatement statement = getConn().prepareStatement(EXISTS_SQL);
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
        getConn().setAutoCommit(false);
        PreparedStatement statement = getConn().prepareStatement(INSERT_SQL);
        statement.setString(1, cust.getMobile());
        statement.setString(2, cust.getTag1());
        statement.setString(3, cust.getSource_from());
        statement.setString(4, cust.getIsFirst());
        boolean ret = statement.execute();
        getConn().commit();
        statement.close();
        return ret;
    }

    public boolean insert(OtherCust cust) throws Exception {
        if (exists(cust)) {
            System.out.println("exists:" + cust.getMobile() + " " + cust.getSource_from());
            return false;
        } else {
            String isFirst = isFirst(cust.getMobile()) ? "1" : "0";
            cust.setIsFirst(isFirst);
            return doInsert(cust);
        }
    }
    public static void main(String[] args) throws Exception {
        JDBCHelper jdbc = new JDBCHelper("jdbc:postgresql://10.10.2.220:5432/trade", "zlfund", "zlfund");
        //OtherCust cust = new OtherCust("13900053890", "话费", "ZL");
        //jdbc.insert(cust);
        List<OtherCust> custs = new ArrayList<>();

        File f = new File("D:/other.csv");
        List lines = FileUtils.readLines(f,"utf-8");
        for (Object line : lines) {
            String[] attrArray = line.toString().split(",");
            custs.add(new OtherCust(attrArray[1], attrArray[0], attrArray[2]));
        }
        System.out.println(custs.get(0));
        System.out.println(custs.size());
        int i = 0;
        for (OtherCust cust : custs) {
            jdbc.insert(cust);
            System.out.println(i++);
        }
    }

}
