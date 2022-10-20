package com.example.tenpo.service;

import com.example.tenpo.repo.rest.PercentageRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SumService {


    private PercentageRepository percentageRepository;


    public SumService(TimeUtils timeUtils, PercentageRepository percentageRepository) {
        this.percentageRepository = percentageRepository;
    }

    public Optional<Integer> sum(int a, int b){
        Optional<Integer> percentageOpt = percentageRepository.getPercentage();
        return percentageOpt.map(p->calculate(a,b,p));
    }

    private Integer calculate(int a, int b, Integer percentage) {
        int sum = a + b;
        int appliedPercentage = sum * percentage / 100;
        return sum + appliedPercentage;
    }

}
