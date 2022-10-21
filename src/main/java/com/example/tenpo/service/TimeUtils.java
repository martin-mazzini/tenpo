package com.example.tenpo.service;

import java.time.LocalDateTime;

public interface TimeUtils {
    //returns 0 for first half hour, 1 for second half hour
    int getHalfHour(LocalDateTime date);

    //ignores time zone information
    LocalDateTime parseLocalDateTime(String s);

    LocalDateTime getNow();
}
