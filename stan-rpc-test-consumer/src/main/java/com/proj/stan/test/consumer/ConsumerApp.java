package com.proj.stan.test.consumer;

import com.proj.stan.connect.anno.RpcScan;
import com.proj.stan.test.consumer.controller.HelloController;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @Author: stan
 * @Date: 2021/02/20
 * @Description:
 */
@RpcScan(basePackage = "com.proj.stan")
public class ConsumerApp {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ConsumerApp.class);
        HelloController controller = context.getBean(HelloController.class);
        controller.hello("monica");
    }
}
