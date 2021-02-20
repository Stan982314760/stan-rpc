package com.proj.stan.connect.loadbalance.impl;

import com.proj.stan.connect.loadbalance.LoadBalancer;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @Author: stan
 * @Date: 2021/02/19
 * @Description:
 */
public class RandomLoadBalancer implements LoadBalancer {

    @Override
    public InetSocketAddress chooseServer(List<String> childrenPathList) {
        int index = ThreadLocalRandom.current().nextInt(childrenPathList.size());
        String path = childrenPathList.get(index);
        String[] split = path.split(":");
        return new InetSocketAddress(split[0], Integer.valueOf(split[1]));
    }
}
