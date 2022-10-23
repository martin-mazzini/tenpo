package com.example.tenpo.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseBuilder;
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
public class Swagger {


    public static final Response ERROR_401 = new ResponseBuilder().code("401")
            .description("Unauthenticated user")
            .build();

    public static final Response ERROR_403 = new ResponseBuilder().code("403")
            .description("Unauthorized user")
            .build();

    public static final Response ERROR_400 = new ResponseBuilder().code("400")
            .description("Bad request")
            .build();

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .securityContexts(Arrays.asList(securityContext()))
                .securitySchemes(Arrays.asList(apiKey()))
                .useDefaultResponseMessages(false)
                .globalResponses(HttpMethod.POST, globalPostMessages())
                .globalResponses(HttpMethod.GET, globalGetMessages())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.example.tenpo"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiDetails());
    }

    private List<Response> globalGetMessages() {
        return List.of(ERROR_401,ERROR_403, ERROR_400);
    }

    private List<Response> globalPostMessages() {return List.of(ERROR_401,ERROR_403, ERROR_400);}


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


    private SecurityContext securityContext() {
        return SecurityContext.builder().securityReferences(defaultAuth()).build();
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Arrays.asList(new SecurityReference("JWT", authorizationScopes));
    }


    private ApiKey apiKey() {
        return new ApiKey("JWT", "Authorization", "header");
    }




}
