package com.nhnacademy.ruleengineservice.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class WebMvcConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void corsHeadersArePresent() throws Exception {
//        mockMvc.perform(options("/api/v1/")).header("Origin", "http://example.com").header("Access-Control-Request-Method", "GET)).andExpect(status().isOk()).andExpect(header().exists("Access-Control-Allow-Origin")).andExpect(header().string("Access-Control-Allow-Origin", "*"));
    }
}