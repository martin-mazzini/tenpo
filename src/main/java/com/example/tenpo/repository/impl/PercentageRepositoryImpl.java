package com.example.tenpo.repository.impl;

import com.example.tenpo.repository.PercentageRepository;
import com.example.tenpo.service.TimeUtils;
import com.github.benmanes.caffeine.cache.Cache;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class PercentageRepositoryImpl implements PercentageRepository {


    private volatile CacheEntry lastCacheEntry;
    private Cache<String, Integer> percentageCache;
    public static final String SEPARATOR = "-";
    private final String percentageServiceURL = "http://...";
    private TimeUtils timeUtils;
    private RestTemplate restTemplate;

    protected static class CacheEntry {
        private Integer percentage;
        private String key;
        public CacheEntry(Integer percentage, String key) {
            this.percentage = percentage;
            this.key = key;
        }
    }

    public PercentageRepositoryImpl(TimeUtils timeUtils, Cache<String, Integer> percentageCache, RestTemplate restTemplate) {
        this.timeUtils = timeUtils;
        this.percentageCache = percentageCache;
        this.restTemplate = restTemplate;
    }

    @Override
    public Optional<Integer> getPercentage() {
        LocalDateTime now = timeUtils.getNow();
        String cacheKey = getKey(now);
        Integer percentage = percentageCache.getIfPresent(cacheKey);
        if (percentage == null) {
            percentage = fetchPercentage(cacheKey);
        }
        return Optional.ofNullable(percentage);
    }


    public String getKey(LocalDateTime date) {
        return new StringBuffer()
                .append(date.getMonthValue())
                .append(SEPARATOR)
                .append(date.getDayOfMonth())
                .append(SEPARATOR)
                .append(date.getHour())
                .append(SEPARATOR)
                .append(timeUtils.getHalfHour(date))
                .toString();
    }

    @CircuitBreaker(name = "percentageCircuitBreaker", fallbackMethod = "fallback")
    @Retry(name = "percentageRetry")
    public Integer fetchPercentage(String cacheKey) {
        Integer newPercentage = restTemplate.getForObject(percentageServiceURL, Integer.class);
        CacheEntry cacheEntry = new CacheEntry(newPercentage, cacheKey);
        this.lastCacheEntry = cacheEntry;
        percentageCache.put(cacheKey, newPercentage);
        return newPercentage;
    }


    public Integer fetchPercentageFallback(Exception e) {
        return lastCacheEntry != null ? lastCacheEntry.percentage : null;
    }
}