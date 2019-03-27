package com.blackstone.dailyresearch.nio.proto.v1;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NIOClient1 {
    private boolean running = true;
    private SocketChannel channel_rw;
    private Selector selector;

    public static void main(String[] args) throws InterruptedException {
        NIOClient1 nioClient1 = new NIOClient1();
        new Thread(new Runnable() {
            @Override
            public void run() {
                nioClient1.register();
            }
        }).start();
        for (int i = 0; i < 10; i++) {
            nioClient1.send("s" + i);
            Thread.sleep(1000);
        }
        nioClient1.send("bye");
    }

    public void register() {
        try {
            //获取socket通道
            SocketChannel channel = SocketChannel.open();
            channel.configureBlocking(false);

            //获得通道管理器
            selector = Selector.open();
            channel.connect(new InetSocketAddress("127.0.0.1", 8888));
            //为该通道注册SelectionKey.OP_CONNECT事件
            channel.register(selector, SelectionKey.OP_CONNECT);
            while (selector.select() > 0) {
                selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (key.isConnectable()) {
                        channel_rw = (SocketChannel) key.channel();
                        if (channel_rw.isConnectionPending()) {
                            channel_rw.finishConnect();
                        }
                        channel_rw.configureBlocking(false);
                        channel_rw.register(selector, SelectionKey.OP_READ);
                        System.out.println("Connection is established");
                    } else if (key.isReadable()) {
                        SocketChannel channelTmp = (SocketChannel) key.channel();
                        if (!channelTmp.isConnected() || channelTmp.socket().isClosed()) {
                            continue;
                        }
                        ByteBuffer buffer = ByteBuffer.allocate(128);
                        int len = channelTmp.read(buffer);
                        if (len > 0) {
                            byte[] data = buffer.array();
                            String message = new String(data, 0, len);
                            System.out.println("[receive message]size:" + buffer.position() + " msg: " + message);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void send(String msg) {
        try {
            if (channel_rw != null && channel_rw.isConnected()) {
                if (channel_rw.socket() != null && channel_rw.socket().isConnected()) {
                    channel_rw.write(ByteBuffer.wrap(msg.getBytes("utf-8")));
                    if ("bye".equals(msg)) {
                        Thread.sleep(1000);
                        channel_rw.close();
                        selector.wakeup();
                        //running=false;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void close() {

    }
}
