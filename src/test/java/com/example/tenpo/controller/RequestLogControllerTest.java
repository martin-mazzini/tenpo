package com.example.tenpo.controller;

import com.example.tenpo.configuration.AsyncRequestProcessorFilter;
import com.example.tenpo.controller.request.CreateUserRequest;
import com.example.tenpo.controller.request.LoginRequest;
import com.example.tenpo.testutils.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;


@SpringBootTest()
@AutoConfigureMockMvc
class RequestLogControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsersControllerTest usersControllerTest;

/*
    @BeforeEach
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
                .addFilter(asyncRequestProcessorFilter)
                .build();
    }
*/


    @Test
    void getRequestLogs() throws Exception {

        usersControllerTest.performUserCreation("martinmazzinigeo@gmail.com","m1234567");
        usersControllerTest.performLogin("martinmazzinigeo@gmail.com","m1234567");
        this.mockMvc.perform(MockMvcRequestBuilders.get(URI.PING)
                .contentType(APPLICATION_JSON));

        ResultActions perform = this.mockMvc.perform(MockMvcRequestBuilders.get(URI.REQUEST_LOGS)
                .contentType(APPLICATION_JSON));
    }
}
