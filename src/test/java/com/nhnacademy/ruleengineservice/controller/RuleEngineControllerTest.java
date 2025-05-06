package com.nhnacademy.ruleengineservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.ruleengineservice.dto.engine.RuleEvaluationResult;
import com.nhnacademy.ruleengineservice.service.engine.RuleEngineService;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(RuleEngineController.class)
@AutoConfigureMockMvc(addFilters = false)
class RuleEngineControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    RuleEngineService ruleEngineService;

    @Test
    @DisplayName("트리거 기반 룰 평가 및 액션 실행")
    void executeTriggeredRules() throws Exception {
        Map<String, Object> facts = new HashMap<>();
        facts.put("location", "1");
        facts.put("sensor-name", "abc");
        facts.put("comfort", 25.0);

        RuleEvaluationResult result = new RuleEvaluationResult(1L, "테스트 룰", true);
        result.setMessage("룰 조건 충족, 액션 실행 완료");

        Mockito.when(ruleEngineService.executeTriggeredRules(
                eq("AI_DATA_RECEIVED"), Mockito.anyString(), Mockito.any()
        )).thenReturn(List.of(result));

        mockMvc.perform(post("/api/v1/rule-engine/trigger")
                .param("eventType", "AI_DATA_RECEIVED")
                .param("eventParams", "{\"source\":\"LSTM\"}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(facts)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ruleNo").value(1))
                .andExpect(jsonPath("$[0].ruleName").value("테스트 룰"))
                .andExpect(jsonPath("$[0].success").value(true))
                .andExpect(jsonPath("$[0].message").value("룰 조건 충족, 액션 실행 완료"))
                .andDo(print());
    }

    @Test
    @DisplayName("수동 룰 평가 및 액션 실행")
    void executeRule() throws Exception {
        Map<String, Object> facts = new HashMap<>();
        facts.put("location", "1");
        facts.put("sensor-name", "abc");
        facts.put("comfort", 25.0);

        RuleEvaluationResult result = new RuleEvaluationResult(1L, "테스트 룰", true);
        result.setMessage("룰 조건 충족, 액션 실행 완료");

        Mockito.when(ruleEngineService.executeRule(eq(1L), anyMap())).thenReturn(result);

        mockMvc.perform(post("/api/v1/rule-engine/manual/{ruleNo}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(facts)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ruleNo").value(1))
                .andExpect(jsonPath("$.ruleName").value("테스트 룰"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("룰 조건 충족, 액션 실행 완료"))
                .andDo(print());
    }
}