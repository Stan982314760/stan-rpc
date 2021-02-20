package com.proj.stan.connect.remoting.server;

import com.proj.stan.common.entity.RpcServiceProperties;
import com.proj.stan.common.exception.RpcException;
import com.proj.stan.common.factory.SingletonFactory;
import com.proj.stan.connect.provider.ServiceProvider;
import com.proj.stan.connect.provider.impl.ServiceProviderImpl;
import com.proj.stan.connect.remoting.dto.RpcRequest;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * @Author: stan
 * @Date: 2021/02/20
 * @Description:
 */
@Slf4j
public class RequestHandler {

    private final ServiceProvider serviceProvider;

    public RequestHandler() {
        this.serviceProvider = SingletonFactory.getInstance(ServiceProviderImpl.class);
    }

    public Object handleRequest(RpcRequest rpcRequest) {
        RpcServiceProperties properties = rpcRequest.toRpcProperties();
        Object service = serviceProvider.findService(properties);
        return invoke(service, rpcRequest);
    }

    private Object invoke(Object service, RpcRequest rpcRequest) {
        Object result = null;
        try {
            Method method = service.getClass().getDeclaredMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            if (method != null) {
                result = method.invoke(service, rpcRequest.getParameters());
            }
        } catch (Exception e) {
            log.error("request handler invoke method error", e);
            throw new RpcException(e.getMessage(), e);
        }

        return result;
    }
}
