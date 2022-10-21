package com.example.tenpo.repository;

import com.example.tenpo.domain.RequestLog;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class RequestLogRepositoryTest {

    @Autowired
    private RequestLogRepository requestLogRepository;


    @Test
    public void testSave(){
        RequestLog log = RequestLog.builder()
                .elapsedTime(11L)
                .requestBody("{sasa:sasa}")
                .responseBody("{ppp:sss")
                .principal("martinmazzini@gmail.com")
                .responseStatus(200)
                .resourceUri("/users")
                .httpMethod("POST")
                .created(LocalDateTime.now())
                .queryParams("aaaa=aaa")
                .build();
        RequestLog save = requestLogRepository.save(log);
        Optional<RequestLog> found = requestLogRepository.findById(save.getId());
        Assertions.assertThat(found.isPresent());
        Assertions.assertThat(found.get().getId() == 1L);
    }

}
