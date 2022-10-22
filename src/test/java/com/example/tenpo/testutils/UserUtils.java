package com.example.tenpo.testutils;

import com.example.tenpo.controller.request.CreateUserRequest;
import com.example.tenpo.controller.request.LoginRequest;
import com.example.tenpo.domain.UserEntity;
import com.example.tenpo.security.AuthToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.http.MediaType.APPLICATION_JSON;

public class UserUtils {

    private final static ObjectMapper mapper = new ObjectMapper();

    public static UserEntity createUser() {
        return UserEntity.builder()
                .email("martinmazzinigeo@gmail.com")
                .encryptedPassword("83hed8334")
                .firstName("m")
                .lastName("m")
                .userId("aa")
                .build();
    }

    public static ResultActions performUserCreation(String email, String password, MockMvc mockMvc) throws Exception {
        CreateUserRequest createRequest =  CreateUserRequest.builder()
                .email(email)
                .password(password)
                .firstName("Martin")
                .lastName("Mazzini")
                .build();

        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post(URI.USERS)
                .content(mapper.writeValueAsString(createRequest))
                .contentType(APPLICATION_JSON));
        return perform;
    }

    public static ResultActions performLogin(String testEmail, String testPassword, MockMvc mockMvc) throws Exception {
        LoginRequest createRequest = LoginRequest.builder()
                .email(testEmail)
                .password(testPassword)
                .build();

        return mockMvc.perform(MockMvcRequestBuilders.post(URI.USERS_LOGIN)
                .content(mapper.writeValueAsString(createRequest))
                .contentType(APPLICATION_JSON));
    }

    public static void authorizeRequest(ResultActions loginResponse, MockHttpServletRequestBuilder requestBuilder) throws Exception {
        AuthToken authToken = mapper.readValue(loginResponse.andReturn().getResponse().getContentAsString(), AuthToken.class);
        requestBuilder.header("Authorization", authToken.getToken());
    }

    public static void authorizeRequest(MockHttpServletRequestBuilder requestBuilder,MockMvc mockMvc) throws Exception {
        performUserCreation("martinmazzinigeo@gmail.com","m1234567", mockMvc);
        ResultActions tokenResponse = performLogin("martinmazzinigeo@gmail.com", "m1234567", mockMvc);
        authorizeRequest(tokenResponse, requestBuilder);
    }


}
