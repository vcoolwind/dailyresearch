package com.blackstone.dailyresearch.bio.v3;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class BIOServer3 implements Runnable {
    private int count = 0;
    private boolean running = true;

    public static void main(String[] args) {
        new Thread(new BIOServer3()).start();
    }

    @Override
    public void run() {
        try {
            System.out.println("Server started!");
            //backlog 新连接到来不能被接受时，可以最大接受的连接数。
            //举例：如果当前设置为6，则意味着，如果有新的连接到来，但没有得到响应，因为已经有六个连接了，新的连接会立即被拒绝。
            ServerSocket serverSocket = new ServerSocket(8888,6);
            while (running) {
                try {
                    Socket socket = serverSocket.accept();
                    count++;
                    new Handler(socket,count).start();
                    //Thread.sleep(5 * 1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    class Handler extends Thread{
        Socket socket;int count;
        public  Handler(Socket socket,int count){
            this.socket=socket;
            this.count=count;
        }
        @Override
        public void run() {
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
    }

    public void stopService() {
        this.running = false;
    }
}
