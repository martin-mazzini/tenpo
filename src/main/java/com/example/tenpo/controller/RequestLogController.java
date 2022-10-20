package com.example.tenpo.controller;

import com.example.tenpo.controller.response.GetRequestLogsResponse;
import com.example.tenpo.domain.RequestLog;
import com.example.tenpo.service.RequestLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/api/v1/requestlogs")
public class RequestLogController {



    private RequestLogService requestLogService;

    public RequestLogController(RequestLogService requestLogService) {
        this.requestLogService = requestLogService;
    }


    @GetMapping( params = { "page", "size" })
    ResponseEntity<GetRequestLogsResponse> getRequestLogs(@RequestParam("page") int page,
                                                    @RequestParam("size") int size) {
        List<RequestLog> requestLogs = requestLogService.getRequestLogs(page, size);
        return ResponseEntity.ok(new GetRequestLogsResponse(requestLogs));
    }







}
