package com.blackstone.dailyresearch.nio.proto.v1;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NIOServer1 {

    public static void main(String[] args) {
        try {
            //获取一个ServerSocket通道
            ServerSocketChannel serverChannel = ServerSocketChannel.open();
            serverChannel.configureBlocking(false);
            serverChannel.socket().bind(new InetSocketAddress(8888));
            //获取通道管理器
            Selector selector = Selector.open();
            //将通道管理器与通道绑定，并为该通道注册SelectionKey.OP_ACCEPT事件，
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("Server is OK!");
            while (selector.select() > 0) {
                //当有注册的事件到达时，方法返回，否则阻塞。
                // selector.select();
                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    System.out.println("new EVENT:"+key);
                    it.remove();
                    if (key.isAcceptable()) {
                        System.out.println("new Connection!");
                        //有连接事件
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        SocketChannel channel = server.accept();
                        if (channel == null) {
                            continue;
                        }
                        //在与客户端连接成功后，为客户端通道注册SelectionKey.OP_READ事件。
                        channel.configureBlocking(false);
                        channel.register(selector, SelectionKey.OP_READ);

                        channel.write(ByteBuffer.wrap("hello,new channel".getBytes()));
                    } else if (key.isReadable()) {
                        try {
                            //有可读数据事件
                            SocketChannel channel = (SocketChannel) key.channel();
                            if (!channel.isConnected()||channel.socket()==null) {
                                continue;
                            }
                            ByteBuffer buffer = ByteBuffer.allocate(128);
                            int read = channel.read(buffer);
                            if(read==-1){
                                if(channel.socket()!=null){
                                    channel.socket().close();
                                }
                                channel.close();
                                continue;
                            }
                            byte[] data = buffer.array();
                            String message = new String(data,0,buffer.position());
                            System.out.println("[receive message] size:" + buffer.position() + " msg: " + message);
                            channel.write(ByteBuffer.wrap(("echo>> "+message).getBytes()));
                            if("bye".equals(message)){
                                System.out.println("client request close，server disconnect now。");
                                channel.close();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }else{
                        System.out.println("unknown event");
                        System.out.println(key);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
