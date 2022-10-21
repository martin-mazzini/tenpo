package com.example.tenpo.service;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Component
public class TimeUtils {

    //returns 0 for first half hour, 1 for second half hour
    public int getHalfHour(LocalDateTime date) {
        return date.getMinute() <= 29 ? 0 : 1;
    }


    //ignores time zone information
    public LocalDateTime parseLocalDateTime(String s){
        return LocalDateTime.parse(s, DateTimeFormatter.ISO_DATE_TIME);
    }

    public LocalDateTime getNow() {
        return LocalDateTime.now();
    }
}
