package com.nhnacademy.ruleengineservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.ruleengineservice.dto.action.ActionRegisterRequest;
import com.nhnacademy.ruleengineservice.dto.action.ActionResponse;
import com.nhnacademy.ruleengineservice.service.action.ActionService;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Slf4j
@ActiveProfiles("test")
@WebMvcTest(ActionController.class)
class ActionControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    ActionService actionService;

    @Test
    @DisplayName("액션 등록 성공")
    void testRegisterAction() throws Exception {
        ActionRegisterRequest request = new ActionRegisterRequest(
                10L,
                "COMFORT_NOTIFICATION",
                "{\"message\":\"습도가 높습니다!\"}",
                1
        );

        ActionResponse response = new ActionResponse(
                1L,
                10L,
                "COMFORT_NOTIFICATION",
                "{\"message\":\"습도가 높습니다!\"}",
                1
        );

        Mockito.when(actionService.registerAction(Mockito.any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/actions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.actNo").value(1L));
    }

    @Test
    @DisplayName("액션 단건 조회 성공")
    void testGetAction() throws Exception {
        ActionResponse response = new ActionResponse(
                1L,
                10L,
                "COMFORT_NOTIFICATION",
                "{\"message\":\"온도가 높습니다!\"}",
                1
        );

        Mockito.when(actionService.getAction(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/actions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.actNo").value(1L));
    }

    @Test
    @DisplayName("액션 삭제 성공")
    void testDeleteActionByDelete() throws Exception {
        Mockito.doNothing().when(actionService).deleteAction(1L);

        mockMvc.perform(delete("/api/v1/actions/1"))
                .andExpect(status().isNoContent());
    }
}