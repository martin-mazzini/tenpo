package com.example.tenpo.controller;

import com.example.tenpo.controller.response.GetRequestLogsResponse;
import com.example.tenpo.domain.RequestLog;
import com.example.tenpo.service.RequestLogService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
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
    ResponseEntity<GetRequestLogsResponse> getRequestLogs(@RequestParam(value = "page", defaultValue = "0", required = false) Integer page,
                                                          @RequestParam(value = "size", defaultValue = "10", required = false) @Min(1) @Max(50) Integer size) {
        Page<RequestLog> pageResponse = requestLogService.getRequestLogs(page, size);
        GetRequestLogsResponse response = GetRequestLogsResponse.builder()
                .content(pageResponse.getContent())
                .pageNo(pageResponse.getNumber())
                .pageSize(pageResponse.getSize())
                .totalPages(pageResponse.getTotalPages())
                .last(pageResponse.isLast())
                .totalElements(pageResponse.getTotalElements()).build();

        return ResponseEntity.ok(response);
    }







}
