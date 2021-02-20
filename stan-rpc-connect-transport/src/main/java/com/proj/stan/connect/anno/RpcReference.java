package com.proj.stan.connect.anno;

import java.lang.annotation.*;

/**
 * @Author: stan
 * @Date: 2021/02/18
 * @Description: RPC组件引用注解
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RpcReference {

    String group() default "";

    String version() default "";

    String serviceName() default "";
}
