package com.proj.stan.common;

/**
 * @Author: stan
 * @Date: 2021/02/18
 * @Description:
 */
public final class RuntimeUtil {
    private RuntimeUtil() {

    }

    public static int cpus() {
        return Runtime.getRuntime().availableProcessors();
    }
}
