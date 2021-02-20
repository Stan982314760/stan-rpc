package com.proj.stan.connect.provider.impl;

import com.proj.stan.common.entity.RpcServiceProperties;
import com.proj.stan.common.extension.ExtensionLoader;
import com.proj.stan.connect.provider.ServiceProvider;
import com.proj.stan.connect.registry.ServiceRegistry;
import com.proj.stan.connect.remoting.constant.RpcConstant;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: stan
 * @Date: 2021/02/18
 * @Description:
 */
@Slf4j
public class ServiceProviderImpl implements ServiceProvider {

    private final ServiceRegistry serviceRegistry;

    private final Map<String, Object> serviceMap;

    private final Set<String> registeredService;

    public ServiceProviderImpl() {
        this.serviceRegistry = ExtensionLoader.getExtensionLoader(ServiceRegistry.class).getExtension("zk");
        this.serviceMap = new ConcurrentHashMap<>();
        this.registeredService = ConcurrentHashMap.newKeySet();
    }



    @Override
    public void publishService(Object service, RpcServiceProperties properties) {
        properties.setServiceName(service.getClass().getInterfaces()[0].getName());
        // 本地缓存
        String serviceName = properties.toServiceName();
        boolean flag = addService(serviceName, service);
        if (flag) {
            // 发布到ZK
            try {
                String host =  InetAddress.getLocalHost().getHostAddress();
                InetSocketAddress socketAddress = new InetSocketAddress(host, RpcConstant.PORT);
                serviceRegistry.registerService(serviceName, socketAddress);
            } catch (Exception e) {
                log.error("注册[{}]服务出错", service.getClass().getName(), e);
            }
        }
    }

    @Override
    public Object findService(RpcServiceProperties properties) {
        return this.serviceMap.get(properties.toServiceName());
    }


    private boolean addService(String serviceName, Object service) {
        if (registeredService.contains(serviceName)) {
            return false;
        }

        serviceMap.put(serviceName, service);
        return registeredService.add(serviceName);
    }


}
