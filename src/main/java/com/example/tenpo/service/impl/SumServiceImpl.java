package com.example.tenpo.service.impl;

import com.example.tenpo.repository.PercentageRepository;
import com.example.tenpo.service.SumService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SumServiceImpl implements SumService {


    private PercentageRepository percentageRepository;


    public SumServiceImpl(PercentageRepository percentageRepository) {
        this.percentageRepository = percentageRepository;
    }

    @Override
    public Optional<Integer> sumAndApplyPercentage(int a, int b){
        Optional<Integer> percentageOpt = percentageRepository.getPercentage();
        return percentageOpt.map(p-> doSumAndApplyPercentage(a,b,p));
    }

    public Integer doSumAndApplyPercentage(int a, int b, Integer percentage) {
        if (percentage < 0 || percentage > 100){
            throw new IllegalArgumentException("Percentage should be between 0 and 100");
        }
        int sum = a + b;
        int appliedPercentage = sum * percentage / 100;
        return sum + appliedPercentage;
    }

}
