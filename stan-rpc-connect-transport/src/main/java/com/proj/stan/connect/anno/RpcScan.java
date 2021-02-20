package com.proj.stan.connect.anno;

import com.proj.stan.connect.spring.RpcScannerRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @Author: stan
 * @Date: 2021/02/18
 * @Description: RPC扫描注解
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(RpcScannerRegistrar.class)
public @interface RpcScan {

    String[] basePackage() default "";
}
