package com.blackstone.dailyresearch.nio.v1;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class NIOClient1 {
    public static void main(String[] args) {
        try {
            //获取socket通道
            SocketChannel channel = SocketChannel.open();
            channel.configureBlocking(false);
            //获得通道管理器
            Selector selector = Selector.open();
            channel.connect(new InetSocketAddress("127.0.0.1", 8888));
            //为该通道注册SelectionKey.OP_CONNECT事件
            channel.register(selector, SelectionKey.OP_CONNECT);
            while (true) {
                selector.select();
                for (SelectionKey key : selector.selectedKeys()) {
                    if (key.isConnectable()) {
                        SocketChannel channelTmp = (SocketChannel) key.channel();
                        if (channelTmp.isConnectionPending()) {
                            channelTmp.finishConnect();
                        }
                        channelTmp.configureBlocking(false);
                        channelTmp.register(selector, SelectionKey.OP_READ);
                        channelTmp.write(ByteBuffer.wrap("hello ,server".getBytes()));
                    } else if (key.isReadable()) {
                        SocketChannel channelTmp = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(128);
                        int len = channelTmp.read(buffer);
                        byte[] data = buffer.array();
                        String message = new String(data, 0, len);
                        System.out.println("recevie message from server:, size:"
                                + buffer.position() + " msg: " + message);

                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
