package com.example.tenpo.repository.impl;

import com.example.tenpo.repository.PercentageRepository;
import com.example.tenpo.service.TimeUtils;
import com.example.tenpo.service.impl.RequestToDatabaseRunnable;
import com.github.benmanes.caffeine.cache.Cache;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Supplier;

import static com.example.tenpo.repository.impl.PercentageRepositoryImpl.lastCacheEntry;

@Repository
public class PercentageRepositoryImpl implements PercentageRepository {


    protected static volatile CacheEntry lastCacheEntry;
    private Cache<String, Integer> percentageCache;
    public static final String SEPARATOR = "-";
    private final String percentageServiceURL = "http://...";
    private TimeUtils timeUtils;
    private RestTemplate restTemplate;
    private ResilienceAOPHelper resilienceAOPHelper;

    protected static class CacheEntry {
        private Integer percentage;
        private String key;

        public CacheEntry(Integer percentage, String key) {
            this.percentage = percentage;
            this.key = key;
        }

        public Integer getPercentage() {
            return percentage;
        }
    }

    public PercentageRepositoryImpl(TimeUtils timeUtils, Cache<String, Integer> percentageCache, RestTemplate restTemplate, ResilienceAOPHelper resilienceAopHelper) {
        this.timeUtils = timeUtils;
        this.percentageCache = percentageCache;
        this.restTemplate = restTemplate;
        this.resilienceAOPHelper = resilienceAopHelper;
    }

    @Override
    public Optional<Integer> getPercentage() {
        LocalDateTime now = timeUtils.getNow();
        String cacheKey = getKey(now);
        Integer percentage = percentageCache.getIfPresent(cacheKey);
        if (percentage == null) {
            percentage = resilienceAOPHelper.withRetryAndCircuitBreaker(() -> getPercentage(cacheKey));
        }
        return Optional.ofNullable(percentage);
    }

    private Integer getPercentage(String cacheKey) {
        Integer newPercentage = restTemplate.getForObject(percentageServiceURL, Integer.class);
        CacheEntry cacheEntry = new CacheEntry(newPercentage, cacheKey);
        this.lastCacheEntry = cacheEntry;
        percentageCache.put(cacheKey, newPercentage);
        return newPercentage;
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

@Component
class ResilienceAOPHelper {

    private final Logger logger = LoggerFactory.getLogger(ResilienceAOPHelper.class);

   // @CircuitBreaker(name = "percentageCircuitBreaker", fallbackMethod = "fallback")
    @Retry(name = "percentageRetry",  fallbackMethod = "fallback")
    public Integer withRetryAndCircuitBreaker(Supplier<Integer> call) {
        logger.debug("Trying to get percentage");
        return call.get();
    }


    public Integer fallback(Exception e) {
        logger.debug("Fallback method");
        return lastCacheEntry != null ? lastCacheEntry.getPercentage() : null;
    }

}
