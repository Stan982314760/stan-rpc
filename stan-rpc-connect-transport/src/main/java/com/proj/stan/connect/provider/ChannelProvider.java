package com.proj.stan.connect.provider;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;

/**
 * @Author: stan
 * @Date: 2021/02/19
 * @Description:
 */
public interface ChannelProvider {

    Channel createChannel(InetSocketAddress socketAddress);
}
