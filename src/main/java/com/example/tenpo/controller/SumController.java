package com.example.tenpo.controller;


import com.example.tenpo.controller.response.SumResponse;
import com.example.tenpo.security.AuthToken;
import com.example.tenpo.service.SumService;
import com.example.tenpo.service.impl.SumServiceImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
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
    @ApiOperation(value = "Sum two numbers and a percentage applied to them", response = SumResponse.class)
    @ApiResponses(value = {@ApiResponse(code = 503, message = "Downstream service unavailable")})
    public ResponseEntity<SumResponse> sum(
            @ApiParam(value = "First number to sum", example = "5") @RequestParam("a") int a,
            @ApiParam(value = "Second number to sum", example = "10") @RequestParam("b") int b) {
        return sumService.sumAndApplyPercentage(a, b)
                .map(sum -> ResponseEntity.ok(sum))
                .orElse(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build());
    }


}
