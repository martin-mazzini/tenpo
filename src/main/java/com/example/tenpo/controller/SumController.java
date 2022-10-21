package com.example.tenpo.controller;


import com.example.tenpo.service.SumService;
import com.example.tenpo.service.impl.SumServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sum")
public class SumController {


    private SumService sumService;

    public SumController(SumServiceImpl sumService) {
        this.sumService = sumService;
    }


    @GetMapping
    public ResponseEntity<Integer> sum(@RequestParam("a") int a, @RequestParam("b") int b) {
        return sumService.sumAndApplyPercentage(a,b)
                .map(sum -> ResponseEntity.ok(sum))
                .orElse(ResponseEntity.badRequest().build());
    }





}
