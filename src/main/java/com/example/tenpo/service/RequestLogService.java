package com.example.tenpo.service;

import com.example.tenpo.controller.response.GetRequestLogsResponse;

public interface RequestLogService {
    GetRequestLogsResponse getRequestLogs(int pageNo, int pageSize);
}
