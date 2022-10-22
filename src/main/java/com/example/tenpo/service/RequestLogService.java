package com.example.tenpo.service;

import com.example.tenpo.domain.RequestLog;
import org.springframework.data.domain.Page;

public interface RequestLogService {
    Page<RequestLog> getRequestLogs(int pageNo, int pageSize);
}
