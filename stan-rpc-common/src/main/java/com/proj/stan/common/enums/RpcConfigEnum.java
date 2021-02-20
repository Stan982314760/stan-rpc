package com.proj.stan.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: stan
 * @Date: 2021/02/18
 * @Description:
 */
@AllArgsConstructor
@Getter
public enum RpcConfigEnum {

    RPC_CONFIG_PATH("rpc.properties"),
    ZK_ADDRESS("rpc.zookeeper.address");

    ;

    private final String propertyValue;

}
