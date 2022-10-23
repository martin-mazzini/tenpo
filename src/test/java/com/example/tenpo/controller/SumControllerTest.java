package com.example.tenpo.controller;

import com.example.tenpo.controller.response.SumResponse;
import com.example.tenpo.service.SumService;
import com.example.tenpo.testutils.URI;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

import static com.example.tenpo.testutils.AssertionUtils.isNumeric;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest
public class SumControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private WebApplicationContext context;

    @SpyBean
    private SumService sumService;


    @BeforeEach
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
    }


    @Test
    public void whenSum_returnNumericValueAndCode200() throws Exception {
        ResultActions perform = this.mockMvc.perform(MockMvcRequestBuilders.get(URI.SUM)
                .param("a", "10")
                .param("b","2")
                .contentType(APPLICATION_JSON));

        MockHttpServletResponse response = perform.andReturn().getResponse();
        int status = response.getStatus();
        Assertions.assertThat(status).isEqualTo(HttpStatus.OK.value());
        SumResponse sumResponse = mapper.readValue(response.getContentAsString(), SumResponse.class);
        Assertions.assertThat(sumResponse.getA() == 10).isTrue();
    }

    @Test
    public void whenExternalServiceUnavailable_return503() throws Exception {

        Mockito.when(sumService.sumAndApplyPercentage(anyInt(),anyInt())).thenReturn(Optional.empty());

        ResultActions perform = this.mockMvc.perform(MockMvcRequestBuilders.get(URI.SUM)
                .param("a", "10")
                .param("b","2")
                .contentType(APPLICATION_JSON));

        MockHttpServletResponse response = perform.andReturn().getResponse();
        int status = response.getStatus();
        Assertions.assertThat(status).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE.value());
    }





}
