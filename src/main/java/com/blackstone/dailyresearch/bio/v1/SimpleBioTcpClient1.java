package com.blackstone.dailyresearch.bio.v1;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;

/**
 * 基于BIO的Socket客户端
 *
 * @author shirdrn
 */
public class SimpleBioTcpClient1 {

    private String ipAddress;
    private int port;
    private static int pos = 0;

    public SimpleBioTcpClient1() {}

    public SimpleBioTcpClient1(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    /**
     * 连接Socket服务端，并模拟发送请求数据
     * @param data 请求数据
     */
    public void send(byte[] data) {
        Socket socket = null;
        OutputStream out = null;
        InputStream in = null;
        try {
            socket = new Socket(this.ipAddress, this.port); // 连接
            // 发送请求
            out = socket.getOutputStream();
            out.write(data);
            out.flush();
            // 接收响应
            in = socket.getInputStream();
            int totalBytes = 0;
            int receiveBytes = 0;
            byte[] receiveBuffer = new byte[128];
            if((receiveBytes=in.read(receiveBuffer))!=-1) {
                totalBytes += receiveBytes;
            }
            String serverMessage = new String(receiveBuffer, 0, receiveBytes);
            System.out.println("Client: receives serverMessage->" + serverMessage);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // 发送请求并接收到响应，通信完成，关闭连接
                out.close();
                in.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        int n = 5;
        StringBuffer data = new StringBuffer();
        Date start = new Date();
        for(int i=0; i<n; i++) {
            data.delete(0, data.length());
            data.append("I am the client ").append(++pos).append(".");
            SimpleBioTcpClient1 client = new SimpleBioTcpClient1("localhost", 8888);
            client.send(data.toString().getBytes());
        }
        Date end = new Date();
        long cost = end.getTime() - start.getTime();
        System.out.println(n + " requests cost " + cost + " ms.");
    }
}