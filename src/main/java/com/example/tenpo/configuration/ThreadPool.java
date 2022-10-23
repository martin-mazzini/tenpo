package com.example.tenpo.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ThreadPool {


    public static final int N_THREADS = 10;

    @Bean
    public ExecutorService logRequestsExecutor() {
        ExecutorService executorService = Executors.newFixedThreadPool(N_THREADS);
        return executorService;
    }
}
