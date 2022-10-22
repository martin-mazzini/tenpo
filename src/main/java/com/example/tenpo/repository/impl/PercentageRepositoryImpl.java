package com.example.tenpo.repository.impl;

import com.example.tenpo.repository.PercentageRepository;
import com.example.tenpo.service.TimeUtils;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Supplier;

import static com.example.tenpo.repository.impl.PercentageRepositoryImpl.lastPercentage;

@Repository
public class PercentageRepositoryImpl implements PercentageRepository {


    //volatile reference for thread safety
    protected static volatile CacheEntry lastPercentage = new CacheEntry(null,null);
    public static final String SEPARATOR = "-";
    private final String percentageServiceURL = "http://...";
    private TimeUtils timeUtils;
    private RestTemplate restTemplate;
    private ResilienceAOPHelper resilienceAOPHelper;

    
    //immutable, thread safe
    protected static class CacheEntry {
        private Integer percentage;
        private String key;

        public CacheEntry(Integer percentage, String key) {
            this.percentage = percentage;
            this.key = key;
        }

        public Integer getIfMatches(String cacheKey) {
            return (key != null && key.equals(cacheKey)) ? percentage : null;
        }

        public Integer getPercentage() {
            return percentage;
        }
    }

    public PercentageRepositoryImpl(TimeUtils timeUtils,  RestTemplate restTemplate, ResilienceAOPHelper resilienceAopHelper) {
        this.timeUtils = timeUtils;
        this.restTemplate = restTemplate;
        this.resilienceAOPHelper = resilienceAopHelper;
    }

    @Override
    public Optional<Integer> getPercentage() {
        LocalDateTime now = timeUtils.getNow();
        String halfHourCacheKey = getKey(now);
        Integer percentage = lastPercentage.getIfMatches(halfHourCacheKey);
        if (percentage == null) {
            percentage = resilienceAOPHelper.withRetry(() -> getPercentage(halfHourCacheKey));
        }
        return Optional.ofNullable(percentage);
    }

    private Integer getPercentage(String cacheKey) {
        Integer newPercentage = restTemplate.getForObject(percentageServiceURL, Integer.class);
        CacheEntry cacheEntry = new CacheEntry(newPercentage, cacheKey);
        this.lastPercentage = cacheEntry;
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
    public Integer withRetry(Supplier<Integer> call) {
        logger.debug("Trying to get percentage");
        return call.get();
    }


    public Integer fallback(Exception e) {
        logger.debug("Fallback method");
        return lastPercentage != null ? lastPercentage.getPercentage() : null;
    }

}
