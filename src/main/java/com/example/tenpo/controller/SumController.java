package com.example.tenpo.controller;


import com.example.tenpo.controller.response.GetRequestLogsResponse;
import com.example.tenpo.domain.RequestLog;
import com.example.tenpo.service.SumService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sum")
public class SumController {


    private SumService sumService;

    public SumController(SumService sumService) {
        this.sumService = sumService;
    }


    @GetMapping
    public ResponseEntity<Integer> sum(@RequestParam("a") int a, @RequestParam("b") int b) {
        return sumService.sum(a,b)
                .map(sum -> ResponseEntity.ok(sum))
                .orElse(ResponseEntity.badRequest().build());
    }





}
