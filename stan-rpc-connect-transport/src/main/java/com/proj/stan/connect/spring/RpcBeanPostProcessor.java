package com.proj.stan.connect.spring;

import com.proj.stan.common.entity.RpcServiceProperties;
import com.proj.stan.common.factory.SingletonFactory;
import com.proj.stan.connect.anno.RpcReference;
import com.proj.stan.connect.anno.RpcService;
import com.proj.stan.connect.provider.ServiceProvider;
import com.proj.stan.connect.provider.impl.ServiceProviderImpl;
import com.proj.stan.connect.proxy.ServiceProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * @Author: stan
 * @Date: 2021/02/18
 * @Description:
 */
@Component
@Slf4j
public class RpcBeanPostProcessor implements BeanPostProcessor {

    private final ServiceProvider serviceProvider;

    public RpcBeanPostProcessor() {
        serviceProvider = SingletonFactory.getInstance(ServiceProviderImpl.class);
    }

    /**
     * 扫描到@RpcService标注的服务后进行发布
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        RpcService rpcService = bean.getClass().getAnnotation(RpcService.class);
        if (rpcService != null) {
            RpcServiceProperties properties = RpcServiceProperties.builder()
                    .group(rpcService.group())
                    .version(rpcService.version())
                    .build();

            serviceProvider.publishService(bean, properties);
        }

        return bean;
    }


    /**
     * 扫描到@RpcReference标注的服务后进行代理对象创建
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Field[] declaredFields = bean.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            RpcReference rpcReference = field.getDeclaredAnnotation(RpcReference.class);
            if (rpcReference != null) {
                RpcServiceProperties properties = RpcServiceProperties.builder()
                        .version(rpcReference.version())
                        .group(rpcReference.group())
                        .build();

                // 创建代理对象
                ServiceProxy serviceProxy = new ServiceProxy(properties);
                Object proxy = serviceProxy.createProxy(field.getType());
                try {
                    // 反射注入
                    field.setAccessible(true);
                    field.set(bean, proxy);
                } catch (IllegalAccessException e) {
                    log.error("[{}] 注入代理对象 [{}] 出错", beanName, field.getName());
                }

            }
        }

        return bean;
    }
}
