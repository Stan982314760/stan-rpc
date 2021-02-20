package com.proj.stan.connect.spring;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;

/**
 * @Author: stan
 * @Date: 2021/02/18
 * @Description:
 */
public class RpcScanner extends ClassPathBeanDefinitionScanner {


    public RpcScanner(BeanDefinitionRegistry registry) {
        super(registry, true);
    }

    public RpcScanner(BeanDefinitionRegistry registry, Class<? extends Annotation> anno) {
        super(registry, false);
        addIncludeFilter(new AnnotationTypeFilter(anno));
    }


    @Override
    public int scan(String... basePackages) {
        return super.scan(basePackages);
    }
}
