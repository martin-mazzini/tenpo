package com.example.tenpo.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfig {


    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.example.tenpo"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiDetails());
    }





    private ApiInfo apiDetails() {
        return new ApiInfo(
                "Tenpo challenge",
                "Api del challenge de Tenpo",
                "1.0",
                "Libre",
                new springfox.documentation.service.Contact("Martín Mazzini", "https://github.com/martin-mazzini", "martinmazzinigeo@gmail.com"),
                "",
                "",
                Collections.emptyList()

        );
    }


}
