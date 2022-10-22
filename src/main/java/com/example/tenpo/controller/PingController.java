package com.example.tenpo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ping")
public class PingController {


    @GetMapping()
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Healthy instance");
    }




}
