package com.proj.stan.test.provider;

import com.proj.stan.connect.anno.RpcScan;
import com.proj.stan.connect.remoting.server.NettyServer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @Author: stan
 * @Date: 2021/02/20
 * @Description:
 */
@RpcScan(basePackage = "com.proj.stan")
public class ProviderApp {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ProviderApp.class);
        NettyServer nettyServer = new NettyServer();
        nettyServer.start();
    }
}
