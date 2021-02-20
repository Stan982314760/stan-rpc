package com.proj.stan.connect.spring;


import com.proj.stan.connect.anno.RpcScan;
import com.proj.stan.connect.anno.RpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;

/**
 * @Author: stan
 * @Date: 2021/02/18
 * @Description: RPC组件注册器
 */
@Slf4j
public class RpcScannerRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {

    private static final String RPC_BASE_PACKAGE_ATTR_NAME = "basePackage";

    private static final String SPRING_BASE_PACKAGE = "com.proj.stan";

    private ResourceLoader resourceLoader;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }


    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes attributes = AnnotationAttributes
                .fromMap(importingClassMetadata.getAnnotationAttributes(RpcScan.class.getName()));

        String[] basePackage = new String[0];
        if (attributes != null) {
            basePackage = attributes.getStringArray(RPC_BASE_PACKAGE_ATTR_NAME);
        }
        if (basePackage.length == 0) {
            basePackage = new String[]{((StandardAnnotationMetadata) importingClassMetadata)
                    .getIntrospectedClass().getPackage().getName()};
        }

        // 扫描RPC组件
        RpcScanner rpcScanner = new RpcScanner(registry, RpcService.class);
        if (this.resourceLoader != null) {
            rpcScanner.setResourceLoader(this.resourceLoader);
        }
        int rpcComponent = rpcScanner.scan(basePackage);
        log.info("scan rpc component: {}", rpcComponent);

        // 扫描Spring组件
        RpcScanner springScanner = new RpcScanner(registry);
        if (this.resourceLoader != null) {
            springScanner.setResourceLoader(this.resourceLoader);
        }
        int springComponent = springScanner.scan(SPRING_BASE_PACKAGE);
        log.info("scan spring component: {}", springComponent);

    }
}
