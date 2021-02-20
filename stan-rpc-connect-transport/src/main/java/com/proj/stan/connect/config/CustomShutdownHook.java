package com.proj.stan.connect.config;

import com.proj.stan.common.thread.ThreadPoolFactoryUtil;
import com.proj.stan.connect.registry.util.CuratorUtils;

public class CustomShutdownHook {

    private static final CustomShutdownHook CUSTOM_SHUTDOWN_HOOK = new CustomShutdownHook();

    public static CustomShutdownHook getCustomShutdownHook() {
        return CUSTOM_SHUTDOWN_HOOK;
    }

    public void clearAll() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            ThreadPoolFactoryUtil.shutdownAllThreadPool();
            CuratorUtils.clearRegistry(CuratorUtils.getZkClient());
        }));
    }

}