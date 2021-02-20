package com.proj.stan.test.consumer.controller;

import com.proj.stan.connect.anno.RpcReference;
import com.proj.stan.rpc.api.HelloService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Author: stan
 * @Date: 2021/02/20
 * @Description:
 */
@Component
@Slf4j
public class HelloController {

    @RpcReference(group = "test", version = "1.0")
    HelloService helloService;

    public void hello(String name) {
        String sayHi = helloService.sayHi(name);
        log.info("hello controller invoked, the result is : {}", sayHi);
    }
}
