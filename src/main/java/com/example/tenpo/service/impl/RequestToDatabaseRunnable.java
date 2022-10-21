package com.example.tenpo.service.impl;

import com.example.tenpo.domain.RequestLog;
import com.example.tenpo.repository.RequestLogRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.time.LocalDateTime;
import java.util.Optional;

public class RequestToDatabaseRunnable implements Runnable {


    private final int maxPayloadLength = 1000;
    private final RequestLogRepository requestLogRepository;
    private final Long endTime;
    private final Long startTime;
    private final ContentCachingRequestWrapper request;
    private final ContentCachingResponseWrapper response;
    private final Logger logger = LoggerFactory.getLogger(RequestToDatabaseRunnable.class);


    public RequestToDatabaseRunnable(Long startTime, Long endTime, ContentCachingRequestWrapper request, ContentCachingResponseWrapper response, RequestLogRepository requestLogRepository) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.request = request;
        this.response = response;
        this.requestLogRepository = requestLogRepository;
    }



    @Override
    public void run() {
        saveRequestLogToDatabase();
    }

    private void saveRequestLogToDatabase() {
        logger.debug("Starting saving request to db");
        LocalDateTime creationTime = LocalDateTime.now();
        String httpMethod = request.getMethod();
        String requestURI = request.getRequestURI();
        String queryString = request.getQueryString();
        if (queryString != null) {
            requestURI = requestURI + "?" + queryString;
        }

        String requestBody = this.getContentAsString(request.getContentAsByteArray(), this.maxPayloadLength, request.getCharacterEncoding());
        String principal = Optional.ofNullable(request.getUserPrincipal()).map(p ->p.getName()).orElse("");
        Long elapsedTime = endTime-startTime;
        Integer responseStatus = response.getStatus();
        String responseBody = "";
        if (HttpStatus.valueOf(responseStatus).is2xxSuccessful()) {
            byte[] buf = response.getContentAsByteArray();
            responseBody = getContentAsString(buf, this.maxPayloadLength, response.getCharacterEncoding());
        }


        RequestLog log = RequestLog.builder()
                .created(creationTime)
                .httpMethod(httpMethod)
                .resourceUri(requestURI)
                .requestBody(requestBody)
                .principal(principal)
                .responseBody(responseBody)
                .responseStatus(responseStatus)
                .elapsedTime(elapsedTime)
                .build();

        requestLogRepository.save(log);
        logger.debug("Finished saving request to db");
    }



    private String getContentAsString(byte[] buf, int maxLength, String charsetName) {
        if (buf == null || buf.length == 0) return "";
        int length = Math.min(buf.length, this.maxPayloadLength);
        try {
            return new String(buf, 0, length, charsetName);
            // return new String(buf, 0, length, charsetName);
        } catch (Exception ex) {
            return "Unsupported Encoding";
        }
    }

}
