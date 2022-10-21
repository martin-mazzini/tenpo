package com.example.tenpo.service;

import com.example.tenpo.service.impl.TimeUtilsImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.time.LocalDateTime;

class TimeUtilsTest {

    private TimeUtils timeUtils = new TimeUtilsImpl();

    @ParameterizedTest
    @CsvFileSource(resources = "/testdata/service/get_half_hour.csv", numLinesToSkip = 1)
    void testGetHalfHour(String time, Integer expected) {
        int halfHour = timeUtils.getHalfHour(timeUtils.parseLocalDateTime(time));
        Assertions.assertThat(halfHour).isEqualTo(expected);
    }


    @Test
    void testParseDate() {
        LocalDateTime localDateTime = timeUtils.parseLocalDateTime("2000-01-01T00:00:00Z");
        Assertions.assertThat(localDateTime.getDayOfMonth() == 1);
        Assertions.assertThat(localDateTime.getSecond() == 0);
    }
}
