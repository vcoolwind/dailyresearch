package com.blackstone.dailyresearch.jdbc;

import com.blackstone.dailyresearch.util.ConsoleLog;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Types;

public class JDBCHelper {

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

    }

    private synchronized Connection getConn() throws Exception {
        Class.forName("org.postgresql.Driver").newInstance();
        Connection conn = DriverManager.getConnection(url, user, password);
        return conn;
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
