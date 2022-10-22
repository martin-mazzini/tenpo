package com.example.tenpo.controller.response;

import com.example.tenpo.domain.RequestLog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetRequestLogsResponse {

    private List<RequestLog> requestLog;
}
