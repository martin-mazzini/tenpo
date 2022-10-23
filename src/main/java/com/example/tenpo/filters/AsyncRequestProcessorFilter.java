package com.example.tenpo.filters;


import com.example.tenpo.domain.RequestLog;
import com.example.tenpo.service.impl.RequestToDatabaseRunnable;
import com.example.tenpo.repository.RequestLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

@Component
public class AsyncRequestProcessorFilter extends OncePerRequestFilter {

    private final RequestLogRepository requestLogRepository;
    private final ExecutorService threadPool;
    private final Logger logger = LoggerFactory.getLogger(AsyncRequestProcessorFilter.class);

    public AsyncRequestProcessorFilter(RequestLogRepository requestLogRepository, ExecutorService executorService) {
        this.requestLogRepository = requestLogRepository;
        this.threadPool = executorService;
    }


    private final int maxPayloadLength = 5000;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return !path.startsWith("/api/v1/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, ServletException, IOException {

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        long startTime = System.currentTimeMillis();
        LocalDateTime requestDateTime = LocalDateTime.now();

        filterChain.doFilter(wrappedRequest, wrappedResponse);

        long endTime = System.currentTimeMillis();
        RequestLog requestLog = extractData(wrappedRequest,wrappedResponse, startTime, endTime, requestDateTime);
        wrappedResponse.copyBodyToResponse();

        logger.debug("Finished processing request");
        Runnable saveToDB = new RequestToDatabaseRunnable(requestLog, requestLogRepository);
        threadPool.submit(saveToDB);
        logger.debug("Task submited");

    }

    private RequestLog extractData(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response, long startTime, long endTime, LocalDateTime requestStartTime) {

        String httpMethod = request.getMethod();
        String requestURI = request.getRequestURI();
        String queryString = request.getQueryString();
        if (queryString != null) {
            requestURI = requestURI + "?" + queryString;
        }

        String requestBody = this.getContentAsString(request.getContentAsByteArray(), this.maxPayloadLength, request.getCharacterEncoding());
        String principal = Optional.ofNullable(request.getUserPrincipal()).map(p ->p.getName()).orElse("");
        Integer responseStatus = response.getStatus();
        String responseBody = "";
        if (HttpStatus.valueOf(responseStatus).is2xxSuccessful()) {
            byte[] buf = response.getContentAsByteArray();
            responseBody = getContentAsString(buf, this.maxPayloadLength, response.getCharacterEncoding());
        }

        return RequestLog.builder()
                .elapsedTime(endTime-startTime)
                .created(requestStartTime)
                .httpMethod(httpMethod)
                .resourceUri(requestURI)
                .requestBody(requestBody)
                .principal(principal)
                .responseBody(responseBody)
                .responseStatus(responseStatus)
                .build();
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
