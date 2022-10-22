package com.example.tenpo.controller;

import com.example.tenpo.controller.response.GetRequestLogsResponse;
import com.example.tenpo.domain.RequestLog;
import com.example.tenpo.service.RequestLogService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/requestlogs")
public class RequestLogController {


    public static final int MAX_PAGE_SIZE = 50;

    private RequestLogService requestLogService;



    public RequestLogController(RequestLogService requestLogService) {
        this.requestLogService = requestLogService;
    }


    @GetMapping()
    ResponseEntity<GetRequestLogsResponse> getRequestLogs(
            @RequestParam(value = "page", defaultValue = "0", required = false)  @Min(0) Integer page,
            @RequestParam(value = "size", defaultValue = "10", required = false) @Min(1) @Max(MAX_PAGE_SIZE) Integer size) {
        return ResponseEntity.ok(requestLogService.getRequestLogs(page, size));
    }







}
