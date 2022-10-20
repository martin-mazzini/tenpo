package com.example.tenpo.controller.response;

import com.example.tenpo.domain.RequestLog;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class GetRequestLogsResponse {

    private List<RequestLog> requestLog;
}
