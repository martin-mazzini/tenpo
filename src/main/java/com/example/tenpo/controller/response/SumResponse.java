package com.example.tenpo.controller.response;

import io.swagger.models.auth.In;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class SumResponse {


    private Integer a;
    private Integer b;
    private Integer result;
    private Integer appliedPercentage;







}
