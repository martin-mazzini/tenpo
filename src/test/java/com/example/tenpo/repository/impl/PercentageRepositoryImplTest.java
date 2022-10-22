package com.example.tenpo.repository.impl;

import com.example.tenpo.service.TimeUtils;
import com.github.benmanes.caffeine.cache.Cache;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@SpringBootTest
class PercentageRepositoryImplTest {

    public static final Integer MAX_ATTEMPTS = 3;

    @Autowired
    private Cache<String, Integer> percentageCache;

    @SpyBean
    private TimeUtils timeUtils;

    @MockBean
    private RestTemplate restTemplate;


    @Autowired
    private PercentageRepositoryImpl percentageRepository;


    @AfterEach
    public void after(){
        percentageCache.invalidateAll();
    }

    @Test
    void whenCallFirstTime_getFromExternalService() {

        Mockito.when(restTemplate.getForObject(anyString(),any())).thenReturn(12);
        Optional<Integer> percentage = percentageRepository.getPercentage();
        Assertions.assertThat(percentage.isPresent());
        Assertions.assertThat(percentage.get()).isEqualTo(12);

    }

    @Test
    void whenCallSecondTime_getFromCache()  {

        mockTime("2000-03-01T00:00:00Z");
        Mockito.when(restTemplate.getForObject(anyString(),any())).thenReturn(12);
        Optional<Integer> percentage = percentageRepository.getPercentage();
        //still same half hour
        mockTime("2000-03-01T00:29:59Z");
        Mockito.when(restTemplate.getForObject(anyString(),any())).thenReturn(15);
        Optional<Integer> percentage2 = percentageRepository.getPercentage();

        Assertions.assertThat(percentage2.isPresent());
        Assertions.assertThat(percentage2.get()).isEqualTo(12);

    }

    @Test
    void whenHalfHourElapsed_callExternalServiceAgain()  {

        mockTime("2000-03-01T00:00:00Z");
        Mockito.when(restTemplate.getForObject(anyString(),any())).thenReturn(12);
        Optional<Integer> percentage = percentageRepository.getPercentage();
        //still same half hour
        mockTime("2000-03-01T00:30:01Z");
        Mockito.when(restTemplate.getForObject(anyString(),any())).thenReturn(15);
        Optional<Integer> percentage2 = percentageRepository.getPercentage();

        Assertions.assertThat(percentage2.isPresent());
        Assertions.assertThat(percentage2.get()).isEqualTo(15);

    }


    @Test
    void whenServiceBreaks_getLastValueFromCache()  {

        mockTime("2000-03-01T00:00:00Z");
        Mockito.when(restTemplate.getForObject(anyString(),any())).thenReturn(12);
        percentageRepository.getPercentage();

        //half hour elapsed
        mockTime("2000-03-01T00:30:01Z");
        Mockito.when(restTemplate.getForObject(anyString(),any())).thenThrow(RestClientException.class);
        Optional<Integer> percentage = percentageRepository.getPercentage();
        Assertions.assertThat(percentage.isPresent());
        Assertions.assertThat(percentage.get()).isEqualTo(12);
        Mockito.verify(restTemplate, Mockito.times(1 + MAX_ATTEMPTS)).getForObject(anyString(),any());
    }


    @Test
    void whenServiceBreaksAndCacheEmpty_returnEmpty()  {

        mockTime("2000-03-01T00:30:01Z");
        Mockito.when(restTemplate.getForObject(anyString(),any())).thenThrow(RestClientException.class);
        Optional<Integer> percentage = percentageRepository.getPercentage();
        Assertions.assertThat(percentage.isEmpty());
    }

    @Test
    void whenServiceSucceedsThirdTime_returnFromItAndNotFromCache()  {

        mockTime("2000-03-01T00:00:00Z");
        Mockito.when(restTemplate.getForObject(anyString(),any())).thenReturn(12);
        percentageRepository.getPercentage();

        //half hour elapsed
        mockTime("2000-03-01T00:30:01Z");
        Mockito.when(restTemplate.getForObject(anyString(),any())).thenThrow(RestClientException.class)
                .thenThrow(RestClientException.class).thenReturn(20);
        Optional<Integer> percentage = percentageRepository.getPercentage();
        Assertions.assertThat(percentage.isPresent());
        Assertions.assertThat(percentage.get()).isEqualTo(20);
    }

    private void mockTime(String dateTime) {
        LocalDateTime localDateTime = timeUtils.parseLocalDateTime(dateTime);
        Mockito.when(timeUtils.getNow()).thenReturn(localDateTime);
    }


}
