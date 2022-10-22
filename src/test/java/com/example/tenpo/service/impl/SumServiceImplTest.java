package com.example.tenpo.service.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static org.junit.jupiter.api.Assertions.*;

class SumServiceImplTest {

    private SumServiceImpl sumService = new SumServiceImpl(null);

    @ParameterizedTest
    @CsvFileSource(resources = "/testdata/service/sum_and_apply_percentage.csv", numLinesToSkip = 1)
    public void testDoSumAndApplyPercentage(Integer a, Integer b, Integer percentage, Integer expected) {
        Integer result = sumService.doSumAndApplyPercentage(a, b, percentage);
        Assertions.assertThat(result).isEqualTo(expected);
    }

    @Test
    public void whenPercentageOutsideRange_thenThrow() {
        Assertions.assertThatThrownBy(() ->sumService.doSumAndApplyPercentage(1,1,1000))
                .isInstanceOf(IllegalArgumentException.class);
        Assertions.assertThatThrownBy(() ->sumService.doSumAndApplyPercentage(1,1,-1))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
