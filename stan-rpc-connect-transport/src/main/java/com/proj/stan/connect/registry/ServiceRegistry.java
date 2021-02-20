package com.proj.stan.connect.registry;

import com.proj.stan.common.extension.SPI;

import java.net.InetSocketAddress;

/**
 * @Author: stan
 * @Date: 2021/02/18
 * @Description:
 */
@SPI
public interface ServiceRegistry {

    void registerService(String serviceName, InetSocketAddress socketAddress);
}
