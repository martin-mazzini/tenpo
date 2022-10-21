package com.example.tenpo.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.Mockito;

import java.sql.Time;

import static org.junit.jupiter.api.Assertions.*;

class TimeUtilsTest {

    private TimeUtils timeUtils = new TimeUtils();

    @ParameterizedTest
    @CsvFileSource(resources = "/testdata/service/get_half_hour.csv", numLinesToSkip = 1)
    void getHalfHour(String time, Integer expected) {
        int halfHour = timeUtils.getHalfHour(timeUtils.parseLocalDateTime(time));
        Assertions.assertThat(halfHour).isEqualTo(expected);
    }

    @Test
    void getKey() {
    }
}
