package com.proj.stan.connect.loadbalance;

import com.proj.stan.common.extension.SPI;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @Author: stan
 * @Date: 2021/02/19
 * @Description:
 */
@SPI
public interface LoadBalancer {

    InetSocketAddress chooseServer(List<String> childrenPathList);
}
