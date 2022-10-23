package com.example.tenpo.controller;

import com.example.tenpo.controller.response.GetRequestLogsResponse;
import com.example.tenpo.domain.RequestLog;
import com.example.tenpo.security.AuthToken;
import com.example.tenpo.service.RequestLogService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
    @ApiOperation(value = "Get history of all requests", response = GetRequestLogsResponse.class)
    ResponseEntity<GetRequestLogsResponse> getRequestLogs(
            @ApiParam(value = "Page number", example = "0") @RequestParam(value = "page", defaultValue = "0", required = false) @Min(0) Integer page,
            @ApiParam(value = "Page size", example = "20") @RequestParam(value = "size", defaultValue = "10", required = false) @Min(1) @Max(MAX_PAGE_SIZE) Integer size) {
        return ResponseEntity.ok(requestLogService.getRequestLogs(page, size));
    }


}
