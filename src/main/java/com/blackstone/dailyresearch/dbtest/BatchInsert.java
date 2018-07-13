package com.blackstone.dailyresearch.dbtest;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;

public class BatchInsert {

    public static void copyFromFile(Connection connection, String filePath, String tableName)
            throws SQLException, IOException {

        FileInputStream fileInputStream = null;

        try {
            CopyManager copyManager = new CopyManager((BaseConnection)connection);
            fileInputStream = new FileInputStream(filePath);
            copyManager.copyIn("COPY " + tableName + " FROM STDIN", fileInputStream);
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void copyToFile(Connection connection, String filePath, String tableOrQuery)
            throws SQLException, IOException {

        FileOutputStream fileOutputStream = null;

        try {
            CopyManager copyManager = new CopyManager((BaseConnection)connection);
            fileOutputStream = new FileOutputStream(filePath);
            copyManager.copyOut("COPY " + tableOrQuery + " TO STDOUT", fileOutputStream);
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        //test1();
        try {
            Connection conn = null;
            Class.forName("org.postgresql.Driver").newInstance();
            String url = "jdbc:postgresql://10.234.99.245:5432/trade";
            conn = DriverManager.getConnection(url, "zlfund", "zlfund");
            //copyToFile(conn, "d:/data.txt", "(SELECT custno,tradeacco,bank_no,bank_acco from tmp_insertinto_20180404 where custno like 'wyf%')");
            long start = System.currentTimeMillis();
            copyFromFile(conn,"d:/data.txt","tmp_insertinto_20180404");
            long end = System.currentTimeMillis();
            System.out.println("耗时：" + (end - start));
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void test1(){
        try {
            Connection conn = null;
            PreparedStatement stmt = null;

            Class.forName("org.postgresql.Driver").newInstance();
            String url = "jdbc:postgresql://10.234.99.245:5432/trade";
            conn = DriverManager.getConnection(url, "zlfund", "zlfund");

            String SQL = "INSERT INTO tmp_insertinto_20180404(custno,tradeacco,bank_no,bank_acco) " +
                    "VALUES(?, ?, ?, ?)";

            System.out.println("Creating statement...");
            stmt = conn.prepareStatement(SQL);
            conn.setAutoCommit(false);
            long start = System.currentTimeMillis();
            for (int i = 1; i < 400000; i++) {
                stmt.setString(1, "wyfa" + String.valueOf(100000000 + i));
                stmt.setString(2, "wyfb" + String.valueOf(100000000 + i));
                stmt.setString(3, "wyfc" + String.valueOf(100000000 + i));
                stmt.setString(4, "wyfd" + String.valueOf(100000000 + i));
                stmt.addBatch();
                if (i % 7500 == 0) {
                    System.out.println(">>>>"+i);
                    int[] count = stmt.executeBatch();
                    conn.commit();
                    //System.out.println("executeBatch count:" + count.length);
                }
            }

            stmt.executeBatch();
            conn.commit();
            long end = System.currentTimeMillis();
            System.out.println("耗时：" + (end - start));

            stmt.close();
            conn.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
