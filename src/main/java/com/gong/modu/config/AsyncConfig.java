package com.gong.modu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@EnableAsync
@Configuration
public class AsyncConfig {

    // 각 작업을 담당할 스레드 풀 관리
    @Bean("summaryTaskExecutor")
    public Executor summaryTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3); // 기본 스레드 3개 = 영상 3개 동시 처리
        executor.setMaxPoolSize(3); // 최대 3개까지만 허용
        executor.setQueueCapacity(10); // 스레드가 다 바쁠 때 대기열 10개까지 허용
        executor.setThreadNamePrefix("summary-"); // 스레드 이름 구분: summary-N
        executor.initialize();
        return executor;
    }
}
