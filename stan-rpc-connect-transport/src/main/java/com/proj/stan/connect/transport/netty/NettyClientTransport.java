package com.proj.stan.connect.transport.netty;


import com.proj.stan.common.enums.CompressTypeEnum;
import com.proj.stan.common.enums.SerializeTypeEnum;
import com.proj.stan.common.extension.ExtensionLoader;
import com.proj.stan.common.factory.SingletonFactory;
import com.proj.stan.connect.provider.ChannelProvider;
import com.proj.stan.connect.provider.impl.ChannelProviderImpl;
import com.proj.stan.connect.registry.ServiceDiscovery;
import com.proj.stan.connect.remoting.client.UnprocessedRequestCenter;
import com.proj.stan.connect.remoting.constant.RpcConstant;
import com.proj.stan.connect.remoting.dto.RpcMessage;
import com.proj.stan.connect.remoting.dto.RpcRequest;
import com.proj.stan.connect.remoting.dto.RpcResponse;
import com.proj.stan.connect.transport.ClientTransport;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

/**
 * @Author: stan
 * @Date: 2021/02/18
 * @Description:
 */
@Slf4j
public class NettyClientTransport implements ClientTransport {


    private final ServiceDiscovery serviceDiscovery;
    private final ChannelProvider channelProvider;
    private final UnprocessedRequestCenter requestCenter;

    public NettyClientTransport() {
        this.serviceDiscovery = ExtensionLoader.getExtensionLoader(ServiceDiscovery.class).getExtension("zk");
        this.channelProvider = SingletonFactory.getInstance(ChannelProviderImpl.class);
        this.requestCenter = SingletonFactory.getInstance(UnprocessedRequestCenter.class);
    }


    @Override
    public CompletableFuture<RpcResponse<Object>> sendRequest(RpcRequest rpcRequest) {
        // get channel
        String serviceName = rpcRequest.toRpcProperties().toServiceName();
        InetSocketAddress socketAddress = serviceDiscovery.lookupService(serviceName);
        Channel channel = channelProvider.createChannel(socketAddress);
        if (channel == null && !channel.isActive()) {
            log.error("channel state error");
            throw new IllegalStateException("channel state error");
        }

        // unprocessed request
        CompletableFuture<RpcResponse<Object>> future = new CompletableFuture<>();
        requestCenter.put(rpcRequest, future);

        // rpc message
        RpcMessage rpcMessage = RpcMessage.builder()
                .compress(CompressTypeEnum.GZIP.getCode())
                .codec(SerializeTypeEnum.KRYO.getCode())
                .messageType(RpcConstant.REQUEST_TYPE)
                .data(rpcRequest)
                .build();

        // send msg
        channel.writeAndFlush(rpcMessage).addListener((ChannelFuture f) -> {
            if (f.isSuccess()) {
                log.info("netty client send request: {}", rpcRequest);
            } else {
                f.channel().close();
                future.completeExceptionally(f.cause());
                log.error("netty client send request error!!!");
            }
        });

        return future;
    }
}
