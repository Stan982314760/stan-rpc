package com.proj.stan.connect.transport;

import com.proj.stan.common.extension.SPI;
import com.proj.stan.connect.remoting.dto.RpcRequest;
import com.proj.stan.connect.remoting.dto.RpcResponse;

import java.util.concurrent.CompletableFuture;

/**
 * @Author: stan
 * @Date: 2021/02/18
 * @Description:
 */
@SPI
public interface ClientTransport {

    CompletableFuture<RpcResponse<Object>> sendRequest(RpcRequest rpcRequest);
}
