package com.example.tenpo.filters;

import com.example.tenpo.repo.RequestLogRepository;
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
public class AsyncRequestProcessorFilter extends OncePerRequestFilter {

    private final RequestLogRepository requestLogRepository;
    private final ExecutorService threadPool = Executors.newFixedThreadPool(10);
    private final Logger logger = LoggerFactory.getLogger(AsyncRequestProcessorFilter.class);

    public AsyncRequestProcessorFilter(RequestLogRepository requestLogRepository) {
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

        RequestToDatabaseRunnable saveToDB = new RequestToDatabaseRunnable(startTime, endTime, wrappedRequest, wrappedResponse, requestLogRepository);
        logger.debug("Finished processing request");
        threadPool.submit(saveToDB);
        logger.debug("Task submited");
    }




}
