package time_demo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * Created by John on 2018/4/4.
 */
public class TimerServer {
    public static void main(String[] args) throws InterruptedException {
        int port = 8080;
        new TimerServer().bind(port);
    }

    public void bind(int port) throws InterruptedException {
        EventLoopGroup reactor = new NioEventLoopGroup(1);
        EventLoopGroup worker =  new NioEventLoopGroup(3);

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(reactor,worker)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new LineBasedFrameDecoder(1024));
                            socketChannel.pipeline().addLast(new StringDecoder());
                            socketChannel.pipeline().addLast(new TimeServerHandler());
                        }
                    });

            ChannelFuture future = bootstrap.bind(port).sync();
            future.channel().close().sync();
        } finally {
            reactor.shutdownGracefully();
            worker.shutdownGracefully();
        }

    }
}
