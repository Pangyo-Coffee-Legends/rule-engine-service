package com.nhnacademy.ruleengineservice.config;

import com.nhnacademy.ruleengineservice.service.rule.RuleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
class WebMvcConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RuleService ruleService;

    @Test
    @DisplayName("Preflight 요청")
    void testCorsPreflightRequest() throws Exception {
        mockMvc.perform(options("/api/v1/rules")
                        .header("Origin", "https://aiot2.live")
                        .header("Access-Control-Request-Method", "GET")
                        .header("Access-Control-Request-Headers", "content-type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "https://aiot2.live"))
                .andExpect(header().string("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS"))
                .andExpect(header().string("Access-Control-Allow-Headers", "content-type"));
    }

    @Test
    @DisplayName("실제 GET 요청 테스트")
    void testCorsGetRequest() throws Exception {
        when(ruleService.getAllRule()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/rules")
                .header("Origin", "https://aiot2.live"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "https://aiot2.live"));

        verify(ruleService).getAllRule();
    }

    @Test
    @DisplayName("Credentials 테스트 - 없음을 확인")
    void testCorsCredentials() throws Exception {
        mockMvc.perform(get("/api/v1/rules")
                .header("Origin", "https://foreign-domain.com"))
                .andExpect(header().doesNotExist("Access-Control-Allow-Credentials"));
    }
}