package com.example.tenpo.repo.rest;

import com.example.tenpo.service.TimeUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class PercentageRepositoryImpl implements PercentageRepository {


    public static final String SEPARATOR = "-";
    @Value("${service2.url:http://localhost:6060/service2}")
    String serviceUrl;
    private TimeUtils timeUtils;

    public PercentageRepositoryImpl(TimeUtils timeUtils) {
        this.timeUtils = timeUtils;
    }


    @Override
    public Optional<Integer> getPercentage() {
        return Optional.empty();
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
