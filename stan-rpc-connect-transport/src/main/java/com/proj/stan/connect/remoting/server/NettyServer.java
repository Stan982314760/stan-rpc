package com.proj.stan.connect.remoting.server;

import com.proj.stan.common.RuntimeUtil;
import com.proj.stan.common.thread.ThreadPoolFactoryUtil;
import com.proj.stan.connect.config.CustomShutdownHook;
import com.proj.stan.connect.remoting.codec.RpcMessageDecoder;
import com.proj.stan.connect.remoting.codec.RpcMessageEncoder;
import com.proj.stan.connect.remoting.constant.RpcConstant;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.SneakyThrows;

/**
 * @Author: stan
 * @Date: 2021/02/20
 * @Description:
 */
public class NettyServer {

    @SneakyThrows
    public void start() {
        // jvm退出钩子
        CustomShutdownHook.getCustomShutdownHook().clearAll();
        EventLoopGroup boss = new NioEventLoopGroup(1);
        EventLoopGroup worker = new NioEventLoopGroup();
        EventExecutorGroup eventExecutors = new DefaultEventExecutorGroup(
                RuntimeUtil.cpus() * 2,
                ThreadPoolFactoryUtil.createThreadFactory("server-executor", false)
        );

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast("serverIdleHandler", new IdleStateHandler(30 ,0, 0))
                                    .addLast("decoder", new RpcMessageDecoder())
                                    .addLast("encoder", new RpcMessageEncoder())
                                    .addLast(eventExecutors, "serverHandler", new ServerHandler());

                        }
                    });

            ChannelFuture future = bootstrap.bind(RpcConstant.PORT).sync();
            future.channel().closeFuture().sync();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
            eventExecutors.shutdownGracefully();
        }

    }

}
