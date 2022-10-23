package com.example.tenpo.service;

import com.example.tenpo.controller.response.SumResponse;

import java.util.Optional;

public interface SumService {
    Optional<SumResponse> sumAndApplyPercentage(int a, int b);
}
