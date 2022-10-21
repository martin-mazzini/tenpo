package com.example.tenpo.service.impl;

import com.example.tenpo.service.TimeUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Component
public class TimeUtilsImpl implements TimeUtils {

    //returns 0 for first half hour, 1 for second half hour
    @Override
    public int getHalfHour(LocalDateTime date) {
        return date.getMinute() < 30 ? 0 : 1;
    }


    //ignores time zone information
    @Override
    public LocalDateTime parseLocalDateTime(String s){
        return LocalDateTime.parse(s, DateTimeFormatter.ISO_DATE_TIME);
    }

    @Override
    public LocalDateTime getNow() {
        return LocalDateTime.now();
    }
}
