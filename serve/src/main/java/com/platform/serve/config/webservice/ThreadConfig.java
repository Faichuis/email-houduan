package com.platform.serve.config.webservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * 线程池：异步
 *
 * @author lids
 * @version 1.0
 * @date 2021/3/7 19:04
 */
@Configuration
@EnableAsync
public class ThreadConfig {

    @Bean("asyncTaskExecutor")
    public Executor asyncTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(32);
        executor.setQueueCapacity(300);
        executor.initialize();
        return executor;
    }

}
