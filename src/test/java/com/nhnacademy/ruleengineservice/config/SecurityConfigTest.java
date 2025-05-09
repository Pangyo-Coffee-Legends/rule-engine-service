package com.nhnacademy.ruleengineservice.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void comfortEndpoint_shouldPermitAll() throws Exception {
        mockMvc.perform(post("/api/v1/comfort")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"location\":\"A\"}"))
                .andExpect(status().isOk()); // 혹은 기대하는 응답 코드
    }
}