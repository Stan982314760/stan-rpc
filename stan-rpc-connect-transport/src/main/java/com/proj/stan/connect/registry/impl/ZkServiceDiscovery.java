package com.proj.stan.connect.registry.impl;

import com.proj.stan.common.extension.ExtensionLoader;
import com.proj.stan.connect.loadbalance.LoadBalancer;
import com.proj.stan.connect.registry.ServiceDiscovery;
import com.proj.stan.connect.registry.util.CuratorUtils;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @Author: stan
 * @Date: 2021/02/18
 * @Description:
 */
public class ZkServiceDiscovery implements ServiceDiscovery {

    private final LoadBalancer loadBalancer;

    public ZkServiceDiscovery() {
        this.loadBalancer = ExtensionLoader.getExtensionLoader(LoadBalancer.class).getExtension("random");
    }

    @Override
    public InetSocketAddress lookupService(String serviceName) {
        List<String> childrenNodes = CuratorUtils.getChildrenNodes(CuratorUtils.getZkClient(), serviceName);
        InetSocketAddress socketAddress = loadBalancer.chooseServer(childrenNodes);
        return socketAddress;
    }
}
