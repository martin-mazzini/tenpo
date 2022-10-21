package com.example.tenpo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {


    @GetMapping("/ping")
    ResponseEntity<String> health() {
        return ResponseEntity.ok("Healthy instance");
    }




}
