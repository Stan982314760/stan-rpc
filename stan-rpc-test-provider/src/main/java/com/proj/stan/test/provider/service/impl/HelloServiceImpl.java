package com.proj.stan.test.provider.service.impl;

import com.proj.stan.connect.anno.RpcService;
import com.proj.stan.rpc.api.HelloService;

/**
 * @Author: stan
 * @Date: 2021/02/20
 * @Description:
 */
@RpcService(group = "test", version = "1.0")
public class HelloServiceImpl implements HelloService {

    @Override
    public String sayHi(String name) {
        return "hello " + name;
    }
}
