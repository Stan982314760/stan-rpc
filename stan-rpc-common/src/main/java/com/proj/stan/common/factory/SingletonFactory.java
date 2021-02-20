package com.proj.stan.common.factory;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: stan
 * @Date: 2021/02/18
 * @Description:
 */
public final class SingletonFactory {

    private static final Map<String, Object> object_map = new HashMap<>();

    private SingletonFactory() {

    }


    public static <T> T getInstance(Class<T> clazz) {
        String key = clazz.toString();
        Object instance = null;

        try {
            instance = object_map.get(key);
            if (instance == null) {
                synchronized (SingletonFactory.class) {
                    instance = object_map.get(key);
                    if (instance == null) {
                        instance = clazz.getDeclaredConstructor().newInstance();
                        object_map.put(key, instance);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return clazz.cast(instance);
    }

}
