package com.proj.stan.connect.proxy;

import com.proj.stan.common.entity.RpcServiceProperties;
import com.proj.stan.common.enums.RpcExceptionEnum;
import com.proj.stan.common.enums.RpcResponseCodeEnum;
import com.proj.stan.common.exception.RpcException;
import com.proj.stan.common.extension.ExtensionLoader;
import com.proj.stan.connect.remoting.dto.RpcRequest;
import com.proj.stan.connect.remoting.dto.RpcResponse;
import com.proj.stan.connect.transport.ClientTransport;
import lombok.SneakyThrows;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @Author: stan
 * @Date: 2021/02/18
 * @Description:
 */
public class ServiceProxy implements InvocationHandler {

    private static final String INTERFACE_NAME = "interfaceName";

    private final RpcServiceProperties properties;
    private final ClientTransport clientTransport;

    public ServiceProxy(RpcServiceProperties properties) {
        this.properties = properties;
        this.clientTransport = ExtensionLoader.getExtensionLoader(ClientTransport.class).getExtension("netty");
    }


    @Override
    @SneakyThrows
    public Object invoke(Object proxy, Method method, Object[] args) {
        RpcRequest rpcRequest = RpcRequest.builder()
                .interfaceName(proxy.getClass().getInterfaces()[0].getName())
                .methodName(method.getName())
                .paramTypes(method.getParameterTypes())
                .parameters(args)
                .group(properties.getGroup())
                .version(properties.getVersion())
                .requestId(UUID.randomUUID().toString())
                .build();

        CompletableFuture<RpcResponse<Object>> future = clientTransport.sendRequest(rpcRequest);
        RpcResponse<Object> response = future.get();
        checkResponse(response, rpcRequest);
        return response.getData();
    }

    private void checkResponse(RpcResponse<Object> response, RpcRequest rpcRequest) {
        if (response == null) {
            throw new RpcException(RpcExceptionEnum.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }

        if (!rpcRequest.getRequestId().equals(response.getRequestId())) {
            throw new RpcException(RpcExceptionEnum.REQUEST_NOT_MATCH_RESPONSE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }

        if (response.getCode() == null || response.getCode() != RpcResponseCodeEnum.SUCCESS.getCode()) {
            throw new RpcException(RpcExceptionEnum.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }
    }


    public <T>  T createProxy(Class<T> clazz) {
        Object instance = Proxy.newProxyInstance(
                ServiceProxy.class.getClassLoader(),
                new Class[]{clazz},
                this);

        return clazz.cast(instance);
    }
}
