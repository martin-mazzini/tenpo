package com.example.tenpo.service.impl;

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
    public Page<RequestLog> getRequestLogs(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return requestLogRepository.findAll(pageable);
    }

}
