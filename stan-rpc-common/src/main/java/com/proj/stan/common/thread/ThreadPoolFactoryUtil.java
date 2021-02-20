package com.proj.stan.common.thread;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.*;

/**
 * @Author: stan
 * @Date: 2021/02/18
 * @Description:
 */
@Slf4j
public final class ThreadPoolFactoryUtil {
    private static final Map<String, ExecutorService> thread_pool_map = new ConcurrentHashMap<>();


    private ThreadPoolFactoryUtil() {

    }

    public static ExecutorService createThreadPoolIfAbsent(String threadNamePrefix) {
        DefaultThreadPoolConfig threadPoolConfig = new DefaultThreadPoolConfig();
        return createThreadPoolIfAbsent(threadNamePrefix, threadPoolConfig);
    }

    public static ExecutorService createThreadPoolIfAbsent(String threadNamePrefix, DefaultThreadPoolConfig threadPoolConfig) {
        return createThreadPoolIfAbsent(threadNamePrefix, threadPoolConfig, false);
    }

    public static ExecutorService createThreadPoolIfAbsent(String threadNamePrefix, DefaultThreadPoolConfig threadPoolConfig, Boolean daemon) {
        ExecutorService threadPool = thread_pool_map.computeIfAbsent(threadNamePrefix, k -> createThreadPool(threadNamePrefix, threadPoolConfig, daemon));
        if (threadPool.isShutdown() || threadPool.isTerminated()) {
            thread_pool_map.remove(threadNamePrefix);
            threadPool = thread_pool_map.computeIfAbsent(threadNamePrefix, k -> createThreadPool(threadNamePrefix, threadPoolConfig, daemon));
        }

        return threadPool;
    }

    private static ThreadPoolExecutor createThreadPool(String threadNamePrefix, DefaultThreadPoolConfig threadPoolConfig, Boolean daemon) {
        ThreadFactory threadFactory = createThreadFactory(threadNamePrefix, daemon);

        return new ThreadPoolExecutor(
                threadPoolConfig.getCorePoolSize(),
                threadPoolConfig.getMaximumPoolSize(),
                threadPoolConfig.getKeepAliveTime(),
                threadPoolConfig.getUnit(),
                threadPoolConfig.getWorkQueue(),
                threadFactory,
                new ThreadPoolExecutor.DiscardOldestPolicy());
    }

    public static ThreadFactory createThreadFactory(String threadNamePrefix, Boolean daemon) {
        if (threadNamePrefix == null || threadNamePrefix.trim().length() == 0) {
            return Executors.defaultThreadFactory();
        }

        if (daemon == null) {
            return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d").build();
        }

        return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d").setDaemon(daemon).build();
    }


    public static void shutdownAllThreadPool() {
        log.info("start shutdown all thread-pool");
        thread_pool_map.entrySet().parallelStream()
                .forEach(entry -> {
                    ExecutorService threadPool = entry.getValue();
                    threadPool.shutdown();
                    log.info("shutdown [{}] [{}]", entry.getKey(), threadPool.isTerminated());
                    try {
                        threadPool.awaitTermination(10L, TimeUnit.SECONDS);
                    } catch (Exception e) {
                        log.info("shutdown [{}] error", entry.getKey(), e);
                        threadPool.shutdownNow();
                    }
                });
    }

    public static void printThreadPoolStatus(ThreadPoolExecutor threadPool) {
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1,
                createThreadFactory("print-thread", false));

        scheduledThreadPoolExecutor.scheduleAtFixedRate(() -> {
            log.info("======start thread pool status======");
            log.info("ThreadPool Size: [{}]", threadPool.getPoolSize());
            log.info("Active Threads: [{}]", threadPool.getActiveCount());
            log.info("Number of Tasks : [{}]", threadPool.getCompletedTaskCount());
            log.info("Number of Tasks in Queue: {}", threadPool.getQueue().size());
            log.info("======end thread pool status======");
        }, 0, 3, TimeUnit.SECONDS);

    }


}
