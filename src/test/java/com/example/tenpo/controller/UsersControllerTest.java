package com.example.tenpo.controller;

import com.example.tenpo.controller.request.CreateUserRequest;
import com.example.tenpo.controller.request.LoginRequest;
import com.example.tenpo.domain.UserEntity;
import com.example.tenpo.repository.UserRepository;
import com.example.tenpo.testutils.DatabaseResetter;
import com.example.tenpo.testutils.URI;
import com.example.tenpo.testutils.UserUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.xml.crypto.Data;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
class UsersControllerTest {


    public static final String TEST_EMAIL = "martinmazzinigeo@gmail.com";
    public static final String TEST_PASSWORD = "m123456789";
    @Autowired
    private DatabaseResetter databaseResetter;

    @AfterEach
    public void after(){
        databaseResetter.resetDatabaseState();
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WebApplicationContext context;

    private ObjectMapper mapper = new ObjectMapper();


    @Test
    void whenLoginSuccesfully_thenReturn200() throws Exception {

        executeUserCreationRequest();
        LoginRequest createRequest = LoginRequest.builder()
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .build();

        ResultActions perform = this.mockMvc.perform(MockMvcRequestBuilders.post(URI.USERS_LOGIN)
                .content(mapper.writeValueAsString(createRequest))
                .contentType(APPLICATION_JSON));

        int status = perform.andReturn().getResponse().getStatus();
        Assertions.assertThat(status).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void whenLoginAndUserNotExists_thenReturn404() throws Exception {


        LoginRequest createRequest = LoginRequest.builder()
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .build();

        ResultActions perform = this.mockMvc.perform(MockMvcRequestBuilders.post(URI.USERS_LOGIN)
                .content(mapper.writeValueAsString(createRequest))
                .contentType(APPLICATION_JSON));

        int status = perform.andReturn().getResponse().getStatus();
        Assertions.assertThat(status).isEqualTo(HttpStatus.NOT_FOUND.value());

    }

    @Test
    void whenLoginInvalidRequest_thenReturn400() throws Exception {
        LoginRequest createRequest = LoginRequest.builder()
                .email("bad email format")
                .password("tooooooooo long of a password")
                .build();

        ResultActions perform = this.mockMvc.perform(MockMvcRequestBuilders.post(URI.USERS_LOGIN)
                .content(mapper.writeValueAsString(createRequest))
                .contentType(APPLICATION_JSON));

        int status = perform.andReturn().getResponse().getStatus();
        Assertions.assertThat(status).isEqualTo(HttpStatus.BAD_REQUEST.value());

    }


    @Test
    void whenCreateUserSuccesfully_thenReturn200() throws Exception {

        ResultActions perform = executeUserCreationRequest();
        int status = perform.andReturn().getResponse().getStatus();
        Assertions.assertThat(status).isEqualTo(HttpStatus.OK.value());
    }


    @Test
    void whenUserAlreadyExists_thenReturn409() throws Exception {
        executeUserCreationRequest();
        ResultActions result  = executeUserCreationRequest();
        int status = result.andReturn().getResponse().getStatus();
        Assertions.assertThat(status).isEqualTo(HttpStatus.CONFLICT.value());
    }

    @Test
    void whenCreateUserInvalidRequest_thenReturn400() throws Exception {

        CreateUserRequest createRequest =  CreateUserRequest.builder()
                .email("BAD_EMAIL_FORMAT")
                .password("m123456789")
                .firstName("Martin")
                .lastName("Mazzini")
                .build();

        ResultActions perform = this.mockMvc.perform(MockMvcRequestBuilders.post(URI.USERS)
                .content(mapper.writeValueAsString(createRequest))
                .contentType(APPLICATION_JSON));

        int status = perform.andReturn().getResponse().getStatus();
        Assertions.assertThat(status).isEqualTo(HttpStatus.BAD_REQUEST.value());

    }


    private ResultActions executeUserCreationRequest() throws Exception {
        CreateUserRequest createRequest =  CreateUserRequest.builder()
                .email("martinmazzinigeo@gmail.com")
                .password("m123456789")
                .firstName("Martin")
                .lastName("Mazzini")
                .build();

        ResultActions perform = this.mockMvc.perform(MockMvcRequestBuilders.post(URI.USERS)
                .content(mapper.writeValueAsString(createRequest))
                .contentType(APPLICATION_JSON));
        return perform;
    }

}
