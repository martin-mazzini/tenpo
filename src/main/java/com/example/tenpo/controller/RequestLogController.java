package com.example.tenpo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RequestLogController {




    @GetMapping("/ping")
    ResponseEntity<String> health() {
        return ResponseEntity.ok("Healthy instance");
    }







}
