package com.nhnacademy.ruleengineservice.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.ruleengineservice.dto.condition.ConditionRegisterRequest;
import com.nhnacademy.ruleengineservice.dto.condition.ConditionResponse;
import com.nhnacademy.ruleengineservice.service.condition.ConditionService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@ActiveProfiles("test")
@WebMvcTest(ConditionController.class)
class ConditionControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    ConditionService conditionService;

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

        when(conditionService.registerCondition(Mockito.any())).thenReturn(response);

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

        when(conditionService.getCondition(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/conditions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conField").value("TEST Condition"));
    }

    @Test
    @DisplayName("모든 조건 조회")
    void getConditions_ReturnsAllConditions() throws Exception {
        List<ConditionResponse> mockResponses = List.of(
                new ConditionResponse(1L, 10L,"temperature", "GREATER_THAN", "25",1),
                new ConditionResponse(2L, 20L, "humidity", "LESS_THAN", "70", 2)
        );
        when(conditionService.getConditions()).thenReturn(mockResponses);

        MvcResult result = mockMvc.perform(get("/api/v1/conditions"))
                .andExpect(status().isOk())
                .andReturn();

        List<ConditionResponse> responses = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<List<ConditionResponse>>() {}
        );

        assertEquals(2, responses.size());
        assertEquals("temperature", responses.get(0).getConType());
        verify(conditionService, times(1)).getConditions();
    }

    @Test
    @DisplayName("룰에 해당하는 모든 조건 조회")
    void getConditionByRule_ValidRuleNo_ReturnsConditions() throws Exception {
        Long ruleNo = 1L;
        List<ConditionResponse> mockResponses = List.of(
                new ConditionResponse(1L, 10L, "EQ", "pressure1", "1013", 1),
                new ConditionResponse(1L, 10L, "EQ", "pressure2", "1014", 2)
        );
        when(conditionService.getConditionsByRule(ruleNo)).thenReturn(mockResponses);

        MvcResult result = mockMvc.perform(get("/api/v1/conditions/rule/{ruleNo}", ruleNo))
                .andExpect(status().isOk())
                .andReturn();

        List<ConditionResponse> responses = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<List<ConditionResponse>>() {}
        );

        assertEquals(2, responses.size());
        assertEquals("EQ", responses.get(0).getConType());
        verify(conditionService, times(1)).getConditionsByRule(ruleNo);
    }

    @Test
    @DisplayName("조건 삭제 성공")
    void testDeleteCondition() throws Exception {
        Mockito.doNothing().when(conditionService).deleteCondition(1L);

        mockMvc.perform(delete("/api/v1/conditions/1"))
                .andExpect(status().isNoContent());
    }
}