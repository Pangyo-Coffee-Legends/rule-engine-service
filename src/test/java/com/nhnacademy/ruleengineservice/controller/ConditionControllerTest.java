package com.nhnacademy.ruleengineservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.ruleengineservice.dto.condition.ConditionRegisterRequest;
import com.nhnacademy.ruleengineservice.dto.condition.ConditionResponse;
import com.nhnacademy.ruleengineservice.interceptor.AuthInterceptor;
import com.nhnacademy.ruleengineservice.service.condition.ConditionService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@ActiveProfiles("test")
@WebMvcTest(ConditionController.class)
@AutoConfigureMockMvc(addFilters = false)
class ConditionControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    ConditionService conditionService;

    @MockitoBean
    private AuthInterceptor authInterceptor;

    @BeforeEach
    void setUp() throws Exception {
        Mockito.when(authInterceptor.preHandle(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(true);
    }

    @Test
    @DisplayName("조건 등록 성공")
    void testRegisterCondition() throws Exception {
        ConditionRegisterRequest request = new ConditionRegisterRequest(
                1L,
                "EQ",
                "TEST Condition",
                "100",
                1
        );

        ConditionResponse response = new ConditionResponse(
                100L,
                1L,
                "EQ",
                "TEST Condition",
                "100",
                1
        );

        Mockito.when(conditionService.registerCondition(Mockito.any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/conditions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.conField").value("TEST Condition"));
    }

    @Test
    @DisplayName("조건 단건 조회 성공")
    void testGetCondition() throws Exception {
        ConditionResponse response = new ConditionResponse(
                1L,
                100L,
                "EQ",
                "TEST Condition",
                "100",
                1
        );

        Mockito.when(conditionService.getCondition(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/conditions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conField").value("TEST Condition"));
    }

    @Test
    @DisplayName("조건 삭제 성공")
    void testDeleteCondition() throws Exception {
        Mockito.doNothing().when(conditionService).deleteCondition(1L);

        mockMvc.perform(delete("/api/v1/conditions/1"))
                .andExpect(status().isNoContent());
    }
}