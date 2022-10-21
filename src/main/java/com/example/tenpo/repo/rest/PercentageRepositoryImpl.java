package com.example.tenpo.repo.rest;

import com.example.tenpo.service.TimeUtils;
import com.github.benmanes.caffeine.cache.Cache;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class PercentageRepositoryImpl implements PercentageRepository {


    public static final String SEPARATOR = "-";
    @Value("${service2.url:http://localhost:6060/service2}")
    String serviceUrl;
    private TimeUtils timeUtils;
    private CacheEntry lastCacheEntry;
    private Cache<String, Integer> percentageCache;
    private RestTemplate restTemplate;

    protected static class CacheEntry{
        private Integer percentage;
        private String key;
        private LocalDateTime insertionTime;

        public CacheEntry(Integer percentage, String key, LocalDateTime insertionTime) {
            this.percentage = percentage;
            this.key = key;
            this.insertionTime = insertionTime;
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
        if (percentage == null){
            percentage = fetchPercentage(cacheKey, now);
        }
        return Optional.ofNullable(percentage);
    }


    @CircuitBreaker(name = "percentageCircuitBreaker", fallbackMethod = "fallback")
    @Retry(name = "percentageRetry")
    public Integer fetchPercentage(String cacheKey, LocalDateTime now) {
        Integer percentage = restTemplate.getForObject(serviceUrl, Integer.class);
        synchronized {
            CacheEntry cacheEntry = new CacheEntry(percentage, cacheKey, now);
        }
        return percentage;

    }

    public synchronized Integer fetchPercentageFallback(Exception e) {
        if (lastCacheEntry == null) {
            return lastCacheEntry != null ? lastCacheEntry.percentage : null;
        }
        return null;
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
}
