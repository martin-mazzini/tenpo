package com.example.tenpo.repository.impl;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Random;

/**
 * Mock for the Percentage Service.
 * Returns a random percentage between 10 and 2
 */
@Component
public class PercentageServiceMock extends RestTemplate {


    private Random random = new Random();

    @Nullable
    public <T> T getForObject(String url, Class<T> responseType, Object... uriVariables) throws RestClientException {
        try {
            return responseType.cast(10 + random.nextInt(2));
        } catch(ClassCastException e) {
            e.printStackTrace();
            return null;
        }
    }

}
