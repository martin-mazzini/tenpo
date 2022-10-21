package com.example.tenpo.service;

import com.example.tenpo.domain.RequestLog;

import java.util.List;

public interface RequestLogService {
    List<RequestLog> getRequestLogs(int pageNo, int pageSize);
}
