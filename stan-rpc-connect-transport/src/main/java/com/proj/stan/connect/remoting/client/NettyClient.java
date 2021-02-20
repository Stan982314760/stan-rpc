package com.proj.stan.connect.remoting.client;

import com.proj.stan.connect.remoting.codec.RpcMessageDecoder;
import com.proj.stan.connect.remoting.codec.RpcMessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

/**
 * @Author: stan
 * @Date: 2021/02/19
 * @Description:
 */
@Slf4j
public final class NettyClient {

    private final EventLoopGroup eventLoopGroup;
    private final Bootstrap bootstrap;

    public NettyClient() {
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new LoggingHandler(LogLevel.INFO))
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast("idleHandler", new IdleStateHandler(0, 5, 0));
                        pipeline.addLast("decoder", new RpcMessageDecoder());
                        pipeline.addLast("encoder", new RpcMessageEncoder());
                        pipeline.addLast("clientHandler", new ClientHandler());
                    }
                });
    }

    @SneakyThrows
    public Channel doConnect(InetSocketAddress socketAddress) {
        CompletableFuture<Channel> future = new CompletableFuture<>();
        bootstrap.connect(socketAddress).addListener((ChannelFuture f) -> {
            if (f.isSuccess()) {
                log.info("netty client connect success");
                future.complete(f.channel());
            } else {
                throw new IllegalStateException("netty client connect fail");
            }
        });

        return future.get();
    }


    public void close() {
        this.eventLoopGroup.shutdownGracefully();
    }
}
