package com.nhnacademy.ruleengineservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.ruleengineservice.dto.comfort.ComfortInfoDTO;
import com.nhnacademy.ruleengineservice.dto.engine.RuleEvaluationResult;
import com.nhnacademy.ruleengineservice.schedule.ComfortInfoBuffer;
import com.nhnacademy.ruleengineservice.service.schedule.ComfortResultService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(ComfortController.class)
class ComfortControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    private ComfortInfoBuffer buffer;

    @MockitoBean
    ComfortResultService comfortResultService;

    @Autowired
    ObjectMapper objectMapper;

    private ComfortInfoDTO testComfortInfo;
    private List<RuleEvaluationResult> testResults;

    @BeforeEach
    void setUp() {
        testComfortInfo = new ComfortInfoDTO(
                "A",
                30.0,
                40.0,
                500.0,
                "덥고 습함",
                "CO2 주의"
        );

        testResults = Collections.singletonList(
                new RuleEvaluationResult(
                        1L,
                        "rule-001",
                        true
                )
        );
    }

    @Test
    @DisplayName("AI 정상 동작")
    void testReceiveComfortInfo() throws Exception {
        mockMvc.perform(post("/api/v1/comfort")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testComfortInfo)))
                .andExpect(status().isOk());

        // buffer.add 가 호출됐는지 검증
        verify(buffer).add(any(ComfortInfoDTO.class));
    }

    @Test
    @DisplayName("스케줄 결과 조회")
    void testGetScheduledResult() throws Exception {
        // Given
        Mockito.when(comfortResultService.getLatestResults())
                .thenReturn(testResults);

        // When & Then
        mockMvc.perform(get("/api/v1/comfort/scheduled-result"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ruleName").value("rule-001"))
                .andExpect(jsonPath("$[0].success").value(true));
    }
}