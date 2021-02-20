package com.proj.stan.connect.registry.impl;

import com.proj.stan.connect.registry.ServiceRegistry;
import com.proj.stan.connect.registry.util.CuratorUtils;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;

/**
 * @Author: stan
 * @Date: 2021/02/18
 * @Description:
 */
public class ZkServiceRegistry implements ServiceRegistry {

    /**
     * socketAddress.toString()  =>   /192.168.1.1:9999
     * @param serviceName
     * @param socketAddress
     */
    @Override
    public void registerService(String serviceName, InetSocketAddress socketAddress) {
        String path = CuratorUtils.ZK_REGISTER_ROOT_PATH + "/" + serviceName + socketAddress.toString();
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        CuratorUtils.createPersistentNode(zkClient, path);
    }
}
