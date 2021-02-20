package com.proj.stan.connect.provider.impl;

import com.proj.stan.common.factory.SingletonFactory;
import com.proj.stan.connect.provider.ChannelProvider;
import com.proj.stan.connect.remoting.client.NettyClient;
import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: stan
 * @Date: 2021/02/19
 * @Description:
 */
public class ChannelProviderImpl implements ChannelProvider {

    private static final Map<String, Channel> channelMap = new ConcurrentHashMap<>();

    private final NettyClient nettyClient;

    public ChannelProviderImpl() {
        this.nettyClient = SingletonFactory.getInstance(NettyClient.class);
    }

    @Override
    public Channel createChannel(InetSocketAddress socketAddress) {
        Channel channel = channelMap.get(socketAddress.toString());
        if (channel != null && channel.isActive()) {
            return channel;
        }

        channelMap.remove(socketAddress.toString());
        Channel newChannel = nettyClient.doConnect(socketAddress);
        channelMap.put(socketAddress.toString(), newChannel);
        return newChannel;
    }
}
