package com.nhnacademy.ruleengineservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.ruleengineservice.dto.comfort.ComfortInfoDTO;
import com.nhnacademy.ruleengineservice.schedule.ComfortInfoBuffer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ComfortController.class)
@AutoConfigureMockMvc(addFilters = false)
class ComfortControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    private ComfortInfoBuffer buffer;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("AI 정상 동작")
    void testReceiveComfortInfo() throws Exception {
        ComfortInfoDTO comfortInfo = new ComfortInfoDTO(
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
                        .content(objectMapper.writeValueAsString(comfortInfo)))
                .andExpect(status().isOk());

        // buffer.add 가 호출됐는지 검증
        verify(buffer).add(any(ComfortInfoDTO.class));
    }
}