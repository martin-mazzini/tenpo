package com.example.tenpo.repo.rest.mock;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpMessageConverterExtractor;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Random;

@Component
public class PercentageServiceMock extends RestTemplate {


    private Random random = new Random();

    @Nullable
    public <T> T getForObject(String url, Class<T> responseType) throws RestClientException {
        try {
            return responseType.cast(10 + random.nextInt(2));
        } catch(ClassCastException e) {
            e.printStackTrace();
            return null;
        }
    }

}
