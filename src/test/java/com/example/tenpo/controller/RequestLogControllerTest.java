package com.example.tenpo.controller;

import com.example.tenpo.controller.response.GetRequestLogsResponse;
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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.example.tenpo.testutils.UserUtils.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;


@SpringBootTest()
@AutoConfigureMockMvc
class RequestLogControllerTest {


    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DatabaseResetter databaseResetter;

    @AfterEach
    public void after(){
        databaseResetter.resetDatabaseState();
    }

    @Test
    void whenGetRequestLogs_getTheFullListOfRequestsExecuted() throws Exception {

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(URI.PING).contentType(APPLICATION_JSON);
        authorizeRequest(request, mockMvc);

        this.mockMvc.perform(request);

        MockHttpServletRequestBuilder getRequestLogsRequest = MockMvcRequestBuilders.get(URI.REQUEST_LOGS).contentType(APPLICATION_JSON);
        authorizeRequest(getRequestLogsRequest, mockMvc);
        ResultActions perform = this.mockMvc.perform(getRequestLogsRequest);
        MockHttpServletResponse response = perform.andReturn().getResponse();
        GetRequestLogsResponse requestLogs = mapper.readValue(response.getContentAsString(), GetRequestLogsResponse.class);

        Assertions.assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        Assertions.assertThat(requestLogs.getContent().size()).isEqualTo(5);
    }



    @Test
    void whenPageSizeExceedsLimit_return400() throws Exception {
        MockHttpServletRequestBuilder getRequestLogsRequest = MockMvcRequestBuilders.get(URI.REQUEST_LOGS)
                .param("size", "100")
                .contentType(APPLICATION_JSON);
        authorizeRequest(getRequestLogsRequest, mockMvc);
        ResultActions response = this.mockMvc.perform(getRequestLogsRequest);
        Assertions.assertThat(response.andReturn().getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }





}
