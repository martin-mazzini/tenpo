package com.example.tenpo.integration;

import com.example.tenpo.controller.request.LoginRequest;
import com.example.tenpo.repository.UserRepository;
import com.example.tenpo.security.AuthToken;
import com.example.tenpo.testutils.DatabaseResetter;
import com.example.tenpo.testutils.URI;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.example.tenpo.testutils.UserUtils.performLogin;
import static com.example.tenpo.testutils.UserUtils.performUserCreation;
import static java.lang.String.format;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
class SignupLoginIntegrationTest {


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

        performUserCreation(TEST_EMAIL, TEST_PASSWORD, this.mockMvc);
        ResultActions perform = performLogin(TEST_EMAIL, TEST_PASSWORD, this.mockMvc);

        int status = perform.andReturn().getResponse().getStatus();
        Assertions.assertThat(status).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void whenLoginAndUserNotExists_thenReturn404() throws Exception {


        ResultActions perform = performLogin(TEST_EMAIL, TEST_PASSWORD, this.mockMvc);

        int status = perform.andReturn().getResponse().getStatus();
        Assertions.assertThat(status).isEqualTo(HttpStatus.NOT_FOUND.value());

    }


    @Test
    void whenLoginInvalidRequest_thenReturn400() throws Exception {
        ResultActions perform = performLogin("bad email format", "tooooooooo long of a password", this.mockMvc);

        int status = perform.andReturn().getResponse().getStatus();
        Assertions.assertThat(status).isEqualTo(HttpStatus.BAD_REQUEST.value());

    }


    @Test
    void whenCreateUserSuccesfully_thenReturn200() throws Exception {

        ResultActions perform = performUserCreation(TEST_EMAIL, TEST_PASSWORD, this.mockMvc);
        int status = perform.andReturn().getResponse().getStatus();
        Assertions.assertThat(status).isEqualTo(HttpStatus.OK.value());
    }


    @Test
    void whenUserAlreadyExists_thenReturn409() throws Exception {
        performUserCreation(TEST_EMAIL, TEST_PASSWORD, this.mockMvc);
        ResultActions result  = performUserCreation(TEST_EMAIL, TEST_PASSWORD, this.mockMvc);
        int status = result.andReturn().getResponse().getStatus();
        Assertions.assertThat(status).isEqualTo(HttpStatus.CONFLICT.value());
    }

    @Test
    void whenCreateUserInvalidRequest_thenReturn400() throws Exception {

        ResultActions perform = performUserCreation("BAD_EMAIL_FORMAT", TEST_PASSWORD, this.mockMvc);

        int status = perform.andReturn().getResponse().getStatus();
        Assertions.assertThat(status).isEqualTo(HttpStatus.BAD_REQUEST.value());

    }





    @Test
    void whenNotLogged_thenDontAuthorizeRequest() throws Exception {
        ResultActions perform = this.mockMvc.perform(MockMvcRequestBuilders.get(URI.PING)
                .contentType(APPLICATION_JSON));

        int status = perform.andReturn().getResponse().getStatus();
        Assertions.assertThat(status).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void whenLogged_thenAuthorizeRequest() throws Exception {

        ResultActions resultActions = performUserCreation(TEST_EMAIL, TEST_PASSWORD, this.mockMvc);
        ResultActions loginResult = performLogin(TEST_EMAIL, TEST_PASSWORD, this.mockMvc);

        AuthToken authToken = mapper.readValue(loginResult.andReturn().getResponse().getContentAsString(), AuthToken.class);

        ResultActions perform = this.mockMvc.perform(MockMvcRequestBuilders.get(URI.PING)
                .header("Authorization", authToken.getToken())
        );
        MockHttpServletResponse response = perform.andReturn().getResponse();

        int status = perform.andReturn().getResponse().getStatus();
        Assertions.assertThat(status).isEqualTo(HttpStatus.OK.value());
    }





}
