package com.example.tenpo.config;

import com.example.tenpo.domain.RequestLog;
import com.example.tenpo.repo.db.RequestLogRepository;
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

@Component
public class RequestLogger extends OncePerRequestFilter {



    private RequestLogRepository requestLogRepository;

    public RequestLogger(RequestLogRepository requestLogRepository) {
        this.requestLogRepository = requestLogRepository;
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

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return !path.startsWith("/api/v1/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, ServletException, IOException {

        long startTime = System.currentTimeMillis();


        //esto se necesita para evitar consumir el input stream antes de ejecutar el request
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        //ejecuta el request
        filterChain.doFilter(wrappedRequest, wrappedResponse);
        long duration = System.currentTimeMillis() - startTime;


        LocalDateTime createdTime = LocalDateTime.now();
        String method = request.getMethod();
        String URI = request.getRequestURI();
        String queryString = request.getQueryString();
        if (queryString != null) {
            URI = URI + "?" + queryString;
        }

        String requestBody = this.getContentAsString(wrappedRequest.getContentAsByteArray(), this.maxPayloadLength, request.getCharacterEncoding());

        String principal = Optional.ofNullable(request.getUserPrincipal()).map(p ->p.getName()).orElse("");
        String authType = request.getAuthType();
        Long elapsedTime = duration;
        Integer responseStatus = response.getStatus();
        String responseBody = "";
        if (HttpStatus.valueOf(responseStatus).is2xxSuccessful()) {
            byte[] buf = wrappedResponse.getContentAsByteArray();
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

        logger.info(log.toString());

        wrappedResponse.copyBodyToResponse();

    }


}
