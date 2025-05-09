package com.nhnacademy.ruleengineservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.ruleengineservice.dto.comfort.ComfortInfoDTO;
import com.nhnacademy.ruleengineservice.service.engine.RuleEngineService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ComfortController.class)
@AutoConfigureMockMvc(addFilters = false)
class ComfortControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    RuleEngineService ruleEngineService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("AI 정상 동작")
    void receiveComfortInfo_success() throws Exception {
        ComfortInfoDTO dto = new ComfortInfoDTO(
                "A",
                LocalDateTime.now(),
                30.0,
                40.0,
                500.0,
                "덥고 습함",
                "CO2 주의"
        );

        mockMvc.perform(post("/api/v1/comfort")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("데이터 정상 수신"));

        ArgumentCaptor<Map<String, Object>> mapCaptor = ArgumentCaptor.forClass(Map.class);
        verify(ruleEngineService).executeTriggeredRules(
                eq("AI_DATA_RECEIVED"),
                eq("{\"source\":\"AI\"}"),
                mapCaptor.capture()
        );

        Map<String, Object> facts = mapCaptor.getValue();
        assertEquals("A", facts.get("location"));
        assertEquals(30.0, facts.get("temperature"));
        assertEquals("CO2 주의", facts.get("co2-comment"));
    }

    @Test
    @DisplayName("제대로 전달되었는지 확인")
    void testReceiveComfortInfo() throws Exception {
        String jsonBody = """
            {
                "location": "A",
                "temperature": 30.0,
                "humidity": 40.0,
                "co2": 500.0,
                "comport-index": "덥고 습함",
                "co2-comment": "CO2 주의"
            }
            """;

        mockMvc.perform(post("/api/v1/comfort")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(content().string("데이터 정상 수신"));

        verify(ruleEngineService).executeTriggeredRules(
                eq("AI_DATA_RECEIVED"),
                eq("{\"source\":\"AI\"}"),
                any()
        );
    }
}