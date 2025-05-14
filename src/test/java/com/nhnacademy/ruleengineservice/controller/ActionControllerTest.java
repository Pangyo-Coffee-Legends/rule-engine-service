package com.nhnacademy.ruleengineservice.controller;

import com.fasterxml.jackson.core.type.TypeReference;
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
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
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

        when(actionService.registerAction(Mockito.any())).thenReturn(response);

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

        when(actionService.getAction(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/actions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.actNo").value(1L));
    }

    @Test
    @DisplayName("모든 액션 조회")
    void getActions_ReturnsActionList() throws Exception {
        List<ActionResponse> mockResponses = List.of(
                new ActionResponse(10L, 1L, "Alert", "Send email", 1),
                new ActionResponse(20L, 2L, "Notification", "Push notification", 2)
        );
        when(actionService.getActions()).thenReturn(mockResponses);

        MvcResult result = mockMvc.perform(get("/api/v1/actions"))
                .andExpect(status().isOk())
                .andReturn();

        List<ActionResponse> responses = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<List<ActionResponse>>() {}
        );

        assertEquals(2, responses.size());
        verify(actionService, times(1)).getActions();
    }

    @Test
    @DisplayName("룰에 해당하는 모든 조회")
    void getActionByRule_ValidRuleNo_ReturnsActionList() throws Exception {
        Long ruleNo = 1L;
        List<ActionResponse> mockResponses = List.of(
                new ActionResponse(1L, 20L,"Alert", "Rule1 Alert", 1),
                new ActionResponse(2L, 20L,"Alert", "Rule2 Alert", 2)
        );
        when(actionService.getActionsByRule(ruleNo)).thenReturn(mockResponses);

        MvcResult result = mockMvc.perform(get("/api/v1/actions/rule/{ruleNo}", ruleNo))
                .andExpect(status().isOk())
                .andReturn();

        List<ActionResponse> responses = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<List<ActionResponse>>() {}
        );

        assertEquals(2, responses.size());
        assertEquals("Alert", responses.get(0).getActType());
        verify(actionService, times(1)).getActionsByRule(ruleNo);
    }

    @Test
    @DisplayName("액션 삭제 성공")
    void testDeleteActionByDelete() throws Exception {
        Mockito.doNothing().when(actionService).deleteAction(1L);

        mockMvc.perform(delete("/api/v1/actions/1"))
                .andExpect(status().isNoContent());
    }
}