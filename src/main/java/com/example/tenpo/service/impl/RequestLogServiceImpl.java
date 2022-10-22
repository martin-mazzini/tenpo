package com.example.tenpo.service.impl;

import com.example.tenpo.controller.response.GetRequestLogsResponse;
import com.example.tenpo.domain.RequestLog;
import com.example.tenpo.repository.RequestLogRepository;
import com.example.tenpo.service.RequestLogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class RequestLogServiceImpl implements RequestLogService {

    private RequestLogRepository requestLogRepository;

    public RequestLogServiceImpl(RequestLogRepository requestLogRepository) {
        this.requestLogRepository = requestLogRepository;
    }


    @Override
    public GetRequestLogsResponse getRequestLogs(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<RequestLog> pageResponse = requestLogRepository.findAll(pageable);
        GetRequestLogsResponse response = GetRequestLogsResponse.builder()
                .content(pageResponse.getContent())
                .pageNo(pageResponse.getNumber())
                .pageSize(pageResponse.getSize())
                .totalPages(pageResponse.getTotalPages())
                .last(pageResponse.isLast())
                .totalElements(pageResponse.getTotalElements()).build();

        return response;
    }

}
