package com.blackstone.dailyresearch.bio.v2;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class BIOServer2 implements Runnable {
    private int count = 0;
    private boolean running = true;

    public static void main(String[] args) {
        new Thread(new BIOServer2()).start();
    }

    @Override
    public void run() {
        try {
            System.out.println("Server started!");
            ServerSocket serverSocket = new ServerSocket(8888);
            while (running) {
                try {
                    Socket socket = serverSocket.accept();
                    count++;
                    handle(socket);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private synchronized void handle(Socket socket) {
        try {
            InputStream in = socket.getInputStream(); // 流：客户端->服务端（读）
            OutputStream out = socket.getOutputStream(); // 流：服务端->客户端（写）
            int receiveBytes;
            byte[] receiveBuffer = new byte[128];
            String clientMessage = "";
            boolean running=true;
            while (running){
                if ((receiveBytes = in.read(receiveBuffer)) != -1) {
                    clientMessage = new String(receiveBuffer, 0, receiveBytes);
                    System.out.println("Server: receives clientMessage->" + clientMessage);
                    if (clientMessage.startsWith("bye")) {
                        System.out.println("Server will disconnect");
                        in.close();
                        out.close();
                        socket.close();
                        running=false;
                    } else {
                        String serverResponseWords =
                                "[" + clientMessage + "]I am the server, and you are the " + (count) + "th client.";
                        out.write(serverResponseWords.getBytes());
                        out.flush();
                    }
                }else{
                    Thread.sleep(100);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void stopService() {
        this.running = false;
    }
}
