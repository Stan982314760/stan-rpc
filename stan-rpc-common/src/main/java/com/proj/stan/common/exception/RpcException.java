package com.proj.stan.common.exception;

import com.proj.stan.common.enums.RpcExceptionEnum;

/**
 * @Author: stan
 * @Date: 2021/02/18
 * @Description:
 */
public class RpcException extends RuntimeException {

    public RpcException(RpcExceptionEnum rpcExceptionEnum) {
        super(rpcExceptionEnum.getMessage());
    }


    public RpcException(RpcExceptionEnum rpcExceptionEnum, String detail) {
        super(rpcExceptionEnum.getMessage() + ":" + detail);
    }



    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }
}
