package com.proj.stan.common;

/**
 * @Author: stan
 * @Date: 2021/02/18
 * @Description:
 */
public class Holder<T> {

    private volatile T value;

    public void set(T t) {
        this.value = t;
    }

    public T get() {
        return this.value;
    }
}
