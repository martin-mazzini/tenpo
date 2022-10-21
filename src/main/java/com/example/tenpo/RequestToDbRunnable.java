package com.example.tenpo;

import com.example.tenpo.domain.RequestLog;
import com.example.tenpo.repo.db.RequestLogRepository;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.time.LocalDateTime;
import java.util.Optional;

public class RequestToDbRunnable implements Runnable{


    private final RequestLogRepository requestLogRepository;
    private Long endTime;
    private Long startTime;
    private ContentCachingRequestWrapper request;
    private ContentCachingResponseWrapper response;
    Logger logger = LoggerFactory.getLogger(RequestToDbRunnable.class);


    public RequestToDbRunnable(Long startTime, Long endTime, ContentCachingRequestWrapper request, ContentCachingResponseWrapper response, RequestLogRepository requestLogRepository) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.request = request;
        this.response = response;
        this.requestLogRepository = requestLogRepository;
    }



    @Override
    public void run() {
        logger.debug("Starting saving request to db");
        LocalDateTime createdTime = LocalDateTime.now();
        String method = request.getMethod();
        String URI = request.getRequestURI();
        String queryString = request.getQueryString();
        if (queryString != null) {
            URI = URI + "?" + queryString;
        }

        String requestBody = this.getContentAsString(request.getContentAsByteArray(), this.maxPayloadLength, request.getCharacterEncoding());

        String principal = Optional.ofNullable(request.getUserPrincipal()).map(p ->p.getName()).orElse("");
        String authType = request.getAuthType();
        Long elapsedTime = endTime-startTime;
        Integer responseStatus = response.getStatus();
        String responseBody = "";
        if (HttpStatus.valueOf(responseStatus).is2xxSuccessful()) {
            byte[] buf = response.getContentAsByteArray();
            responseBody = getContentAsString(buf, this.maxPayloadLength, response.getCharacterEncoding());
        }


        RequestLog log = RequestLog.builder()
                .created(createdTime)
                .httpMethod(method)
                .resourceUri(URI)
                .requestBody(requestBody)
                .principal(principal)
                .authType(authType)
                .responseBody(responseBody)
                .responseStatus(responseStatus)
                .elapsedTime(elapsedTime)
                .build();

        requestLogRepository.save(log);
        logger.debug("Finished saving request to db");
    }

    private int maxPayloadLength = 1000;

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
