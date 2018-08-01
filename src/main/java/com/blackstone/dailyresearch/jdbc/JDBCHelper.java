package com.blackstone.dailyresearch.jdbc;

import com.blackstone.dailyresearch.util.ConsoleLog;
import java.io.File;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;

public class JDBCHelper {
    private static final String OTHER_SQL = "select count(1) from other_cust_summary where mobile= ? ";
    private static final String EXISTS_SQL = "select count(1) from other_cust_summary where mobile= ? and source_from = ?";
    private static final String CUSTINFO_SQL = "select count(1) from custinfo where mobileno= ? ";
    private static final String INSERT_SQL = "INSERT INTO other_cust_summary(mobile, tag1, source_from, isfirst)" +
            "VALUES(?,?,?,?);";
    private static final String incomeSql = "{  call IBP_CALC_INCOME(?,?,?,?) }";
    private static final String EXPENSE_BY_RETAIN_SQL = "{  call IBP_CALC_EXPENSE_BY_RETAIN(?,?,?,?) }";


    private static final String NEXT_WORKDAY_SQL = "{  ?= call IBF_GET_NEXTNWORKDAY2(?,?,?) }";
    private String url;
    private String user;
    private String password;

    public JDBCHelper(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public static void main(String[] args) throws Exception {
        JDBCHelper jdbc = new JDBCHelper("jdbc:postgresql://10.10.99.245:5432/trade", "******", "******");
        List<OtherCust> custs = new ArrayList<>();

        File f = new File("D:/0731.csv");
        List lines = FileUtils.readLines(f, "utf-8");
        for (Object line : lines) {
            String[] attrArray = line.toString().split(",");
            custs.add(new OtherCust(attrArray[0], attrArray[1], attrArray[2]));
        }
        ConsoleLog.println(custs.get(0));
        ConsoleLog.println(custs.size());
        int i = 0;
        for (OtherCust cust : custs) {
            jdbc.insert(cust);
            ConsoleLog.println(i++);
        }
    }

    private synchronized Connection getConn() throws Exception {
        Class.forName("org.postgresql.Driver").newInstance();
        Connection conn = DriverManager.getConnection(url, user, password);
        return conn;
    }

    public boolean isFirst(String mobile) throws Exception {
        return isFirst(mobile, OTHER_SQL) && isFirst(mobile, CUSTINFO_SQL);
    }

    private boolean isFirst(String mobile, String sql) throws Exception {
        Connection conn = getConn();
        PreparedStatement statement = conn.prepareStatement(sql);
        statement.setString(1, mobile);
        ResultSet rs = statement.executeQuery();
        int count = 0;
        if (rs.next()) {
            count = rs.getInt(1);
        }
        rs.close();
        statement.close();
        conn.close();
        if (count > 0) {
            return false;
        } else {
            return true;
        }
    }

    private boolean exists(OtherCust cust) throws Exception {
        Connection conn = getConn();
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
        conn.close();
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean doInsert(OtherCust cust) throws Exception {
        Connection conn = getConn();
        conn.setAutoCommit(false);
        PreparedStatement statement = getConn().prepareStatement(INSERT_SQL);
        statement.setString(1, cust.getMobile());
        statement.setString(2, cust.getTag1());
        statement.setString(3, cust.getSource_from());
        statement.setString(4, cust.getIsFirst());
        boolean ret = statement.execute();
        conn.commit();
        statement.close();
        conn.close();
        return ret;
    }

    public boolean insert(OtherCust cust) throws Exception {
        if (exists(cust)) {
            ConsoleLog.println("exists:" + cust.getMobile() + " " + cust.getSource_from());
            return false;
        } else {
            String isFirst = isFirst(cust.getMobile()) ? "1" : "0";
            cust.setIsFirst(isFirst);
            return doInsert(cust);
        }
    }

    public String callIncomeProc(String startDT, String endDT) throws Exception {
        ConsoleLog.println("IBP_CALC_INCOME(" + startDT + "," + endDT + ") start");
        CallableStatement cStmt = null;
        Connection conn = getConn();

        cStmt = conn.prepareCall(incomeSql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        cStmt.setString(1, startDT);
        cStmt.setString(2, endDT);

        cStmt.registerOutParameter(3, Types.VARCHAR);
        cStmt.registerOutParameter(4, Types.VARCHAR);
        cStmt.execute();
        String errcode = cStmt.getString(3);
        String errmsg = cStmt.getString(4);

        ConsoleLog.println("IBP_CALC_INCOME(" + startDT + "," + endDT + ") end");
        cStmt.close();
        conn.close();
        return "errcode:" + errcode + ";errmsg:" + errmsg;
        //return "ok";
    }

    public String callExpenseByRetainProc(String startDT, String endDT) throws Exception {
        ConsoleLog.println("IBP_CALC_EXPENSE_BY_RETAIN(" + startDT + "," + endDT + ") start");
        CallableStatement cStmt = null;
        Connection conn = getConn();

        cStmt = conn.prepareCall(EXPENSE_BY_RETAIN_SQL, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        cStmt.setString(1, startDT);
        cStmt.setString(2, endDT);

        cStmt.registerOutParameter(3, Types.VARCHAR);
        cStmt.registerOutParameter(4, Types.VARCHAR);
        cStmt.execute();
        String errcode = cStmt.getString(3);
        String errmsg = cStmt.getString(4);

        ConsoleLog.println("IBP_CALC_EXPENSE_BY_RETAIN(" + startDT + "," + endDT + ") end");
        cStmt.close();
        conn.close();
        return "errcode:" + errcode + ";errmsg:" + errmsg;
        //return "ok";
    }


    public String getNextWorkDay(final String inDay, final int offset) throws Exception {
        // PI_WORKDATE IN varchar, --工作
        // PI_NEXTN IN numeric, --第几个工作日
        // PI_FUNDID IN varchar --基金代码 目前废弃
        String nextDay = "";
        Connection conn = null;
        CallableStatement cStmt = null;
        conn = getConn();
        cStmt = conn.prepareCall(NEXT_WORKDAY_SQL, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        cStmt.setString(2, inDay);
        cStmt.setInt(3, offset);
        // 第三个 目前无效
        cStmt.setString(4, "");

        cStmt.registerOutParameter(1, Types.VARCHAR);
        cStmt.execute();
        nextDay = cStmt.getString(1);
        cStmt.close();
        conn.close();

        return nextDay;
    }

}
