package com.example.tenpo.service;

import com.example.tenpo.domain.RequestLog;
import com.example.tenpo.repo.RequestLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class RequestLogService {

    private RequestLogRepository requestLogRepository;

    public RequestLogService(RequestLogRepository requestLogRepository) {
        this.requestLogRepository = requestLogRepository;
    }


    public List<RequestLog> getRequestLogs(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<RequestLog> all = requestLogRepository.findAll(pageable);
        return all.getContent();
    }

}