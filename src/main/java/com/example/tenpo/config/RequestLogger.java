package com.example.tenpo.config;

import com.example.tenpo.repo.db.RequestLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class RequestLogger extends OncePerRequestFilter {



    private RequestLogRepository requestLogRepository;
    private ExecutorService threadPool = Executors.newFixedThreadPool(10);
    Logger logger = LoggerFactory.getLogger(RequestLogger.class);

    public RequestLogger(RequestLogRepository requestLogRepository) {
        this.requestLogRepository = requestLogRepository;
    }


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return !path.startsWith("/api/v1/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, ServletException, IOException {

        long startTime = System.currentTimeMillis();
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);
        filterChain.doFilter(wrappedRequest, wrappedResponse);
        wrappedResponse.copyBodyToResponse();
        long endTime = System.currentTimeMillis();
        RequestToDbRunnable saveToDB = new RequestToDbRunnable(startTime, endTime, wrappedRequest, wrappedResponse, requestLogRepository);
        logger.debug("Finished processing request");

        threadPool.submit(saveToDB);
        logger.debug("Task submited");


    }




}
