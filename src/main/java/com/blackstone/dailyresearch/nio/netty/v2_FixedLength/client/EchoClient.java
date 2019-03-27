package com.blackstone.dailyresearch.nio.netty.v2_FixedLength.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import java.nio.charset.Charset;

public class EchoClient {
    private Channel channel;



    public void start() throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    //.option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            //要确保服务端每次发送都要是4字节，按4字节截断
                            p.addLast(new FixedLengthFrameDecoder(4));
                            p.addLast(new StringEncoder(Charset.forName("utf-8")));
                            p.addLast(new StringDecoder(Charset.forName("utf-8")));
                            //p.addLast("encoder", new MessageEncoder());
                            //p.addLast("decoder", new MessageDecoder());
                            //p.addFirst(new LineBasedFrameDecoder(65535));
                            p.addLast(new EchoClientHandler());
                        }
                    });

            // Start the client.
            ChannelFuture f = b.connect("127.0.0.1", 8888).sync();
            channel = f.channel();
            System.out.println("EchoClient.main Bootstrap配置启动完成");

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
            System.out.println("EchoClient.end");
        } finally {
            // Shut down the event loop to terminate all threads.
            group.shutdownGracefully();
        }
    }

    public boolean isConnect() {
        return channel != null && channel.isActive();
    }

    public void close() {
        if (channel != null) {
            channel.close();
        }
    }

    public void write(Object msg) {
        channel.writeAndFlush(msg);
    }


    public static void main(String[] args) throws InterruptedException {
        EchoClient client = new EchoClient();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    client.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        while (!client.isConnect()) {
            Thread.sleep(1000);
        }

        for (int i = 0; i < 20; i++) {
            String theMsg = "a" + i;
            //byte[] bytes=new byte[50];
            //System.arraycopy(theMsg.getBytes(),0,bytes,0,theMsg.getBytes().length);
            System.out.println("[Client send]:"+theMsg);
            client.write(theMsg);
        }
        Thread.sleep(5000);
        client.close();
    }

}
