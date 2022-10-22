package com.example.tenpo.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ThreadPoolConfig {


    @Bean()
    public ExecutorService logRequestsExecutor() {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        return executorService;
    }
}