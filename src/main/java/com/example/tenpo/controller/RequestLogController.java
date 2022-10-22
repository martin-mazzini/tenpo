package com.example.tenpo.controller;

import com.example.tenpo.controller.response.GetRequestLogsResponse;
import com.example.tenpo.domain.RequestLog;
import com.example.tenpo.service.RequestLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/requestlogs")
public class RequestLogController {



    private RequestLogService requestLogService;
    public RequestLogController(RequestLogService requestLogService) {
        this.requestLogService = requestLogService;
    }


    private static final Integer PAGE_SIZE = 10;



    @GetMapping()
    ResponseEntity<GetRequestLogsResponse> getRequestLogs(@RequestParam(value = "page", defaultValue = "0", required = false) Integer page) {
        List<RequestLog> requestLogs = requestLogService.getRequestLogs(page, PAGE_SIZE);
        return ResponseEntity.ok(new GetRequestLogsResponse(requestLogs));
    }







}
