package com.proj.stan.connect.provider;

import com.proj.stan.common.entity.RpcServiceProperties;

/**
 * @Author: stan
 * @Date: 2021/02/18
 * @Description:
 */
public interface ServiceProvider {

    void publishService(Object service, RpcServiceProperties properties);

    Object findService(RpcServiceProperties properties);

}
