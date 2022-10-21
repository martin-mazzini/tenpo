package com.example.tenpo.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;
@Configuration
public class CaffeineCache {

    public static final int MAXIMUM_SIZE = 100;
    public static final int TTL_1_DAY = 1;

    @Bean
    public Cache<String, Integer> percentagesCache(){
        Cache<String, Integer> cache = Caffeine.newBuilder()
                .expireAfterWrite(TTL_1_DAY, TimeUnit.DAYS)
                .maximumSize(MAXIMUM_SIZE)
                .build();
        return cache;

    }
}
