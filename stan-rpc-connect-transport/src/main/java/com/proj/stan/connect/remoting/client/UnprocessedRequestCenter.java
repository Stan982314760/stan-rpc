package com.proj.stan.connect.remoting.client;

import com.proj.stan.connect.remoting.dto.RpcRequest;
import com.proj.stan.connect.remoting.dto.RpcResponse;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: stan
 * @Date: 2021/02/19
 * @Description:
 */
public class UnprocessedRequestCenter {

    private static final Map<String, CompletableFuture<RpcResponse<Object>>> requestMap = new ConcurrentHashMap<>();


    public void put(RpcRequest rpcRequest, CompletableFuture<RpcResponse<Object>> future) {
        requestMap.put(rpcRequest.getRequestId(), future);
    }


    public void complete(RpcResponse<Object> response) {
        String requestId = response.getRequestId();
        // 这里用remove 而不是get
        CompletableFuture<RpcResponse<Object>> future = requestMap.remove(requestId);
        if (future == null)
            throw new IllegalStateException("unprocessed requests doesn't contain this request");

        future.complete(response);
    }

}
