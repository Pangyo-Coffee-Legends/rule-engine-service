package com.nhnacademy.ruleengineservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.ruleengineservice.dto.rule.RuleRegisterRequest;
import com.nhnacademy.ruleengineservice.dto.rule.RuleResponse;
import com.nhnacademy.ruleengineservice.dto.rule.RuleUpdateRequest;
import com.nhnacademy.ruleengineservice.exception.rule.RuleNotFoundException;
import com.nhnacademy.ruleengineservice.service.rule.RuleService;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@ActiveProfiles("test")
@WebMvcTest(RuleController.class)
class RuleControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    RuleService ruleService;

    @Test
    @DisplayName("규칙 등록 성공 테스트")
    void registerRule() throws Exception {
        RuleRegisterRequest request = new RuleRegisterRequest(1L, "테스트 룰", "설명", 1);

        RuleResponse response = new RuleResponse(1L,
                "테스트 룰",
                "테스트 설명",
                1,
                true,
                1L,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );

        when(ruleService.registerRule(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/rules")
                .header("X-USER", "USER")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ruleNo").value(1))
                .andExpect(jsonPath("$.ruleName").value("테스트 룰"))
                .andDo(print());
    }

    @Test
    @DisplayName("단일 룰 조회 테스트")
    void getRule() throws Exception {
        RuleResponse response = new RuleResponse(1L,
                "테스트 룰",
                "테스트 설명",
                1,
                true,
                1L,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );

        when(ruleService.getRule(Mockito.anyLong())).thenReturn(response);

        mockMvc.perform(get("/api/v1/rules/{ruleNo}", 1L)
                        .header("X-USER", "USER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ruleNo").value(1))
                .andExpect(jsonPath("$.ruleName").value("테스트 룰"))
                .andDo(print());
    }

    @Test
    @DisplayName("특정 그룹의 규칙 목록 조회 성공")
    void getRulesByRuleGroup_Success() throws Exception {
        Long groupNo = 10L;
        RuleResponse rule1 = new RuleResponse(1L, "Rule1", "desc1", 1, true, groupNo,
                List.of(), List.of(), List.of(), List.of(), List.of());
        RuleResponse rule2 = new RuleResponse(2L, "Rule2", "desc2", 2, true, groupNo,
                List.of(), List.of(), List.of(), List.of(), List.of());

        Mockito.when(ruleService.getRulesByGroup(groupNo))
                .thenReturn(Arrays.asList(rule1, rule2));

        mockMvc.perform(get("/api/v1/rules/group/{no}", groupNo)
                        .header("X-USER", "USER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ruleNo").value(1L))
                .andExpect(jsonPath("$[1].ruleNo").value(2L));
    }

    @Test
    @DisplayName("특정 그룹의 규칙 목록이 없을 때 빈 리스트 반환")
    void getRulesByRuleGroup_Empty() throws Exception {
        // Given
        Long groupNo = 99L;
        Mockito.when(ruleService.getRulesByGroup(groupNo))
                .thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/v1/rules/group/{no}", groupNo)
                        .header("X-USER", "USER"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @DisplayName("룰 목록 조회 테스트")
    void getRules() throws Exception {
        List<RuleResponse> responseList = List.of(
                new RuleResponse(1L,"테스트 룰","테스트 설명",1,true,1L,
                        new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),new ArrayList<>()),
                new RuleResponse(2L,"테스트 룰2","테스트 설명2",2,true,1L,
                        new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),new ArrayList<>())
        );

        when(ruleService.getAllRule()).thenReturn(responseList);

        mockMvc.perform(get("/api/v1/rules")
                        .header("X-USER", "USER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ruleNo").value(1))
                .andExpect(jsonPath("$[1].ruleNo").value(2))
                .andExpect(jsonPath("$[0].ruleName").value("테스트 룰"))
                .andExpect(jsonPath("$[1].ruleName").value("테스트 룰2"))
                .andDo(print());
    }

    @Test
    @DisplayName("룰 수정 테스트")
    void updateRule() throws Exception {
        RuleUpdateRequest request = new RuleUpdateRequest("수정된 룰","수정 설명", 2);

        RuleResponse response = new RuleResponse(1L, "수정된 룰", "수정 설명", 2, true, 1L,
                new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),new ArrayList<>());

        when(ruleService.updateRule(Mockito.anyLong(), any())).thenReturn(response);

        mockMvc.perform(put("/api/v1/rules/{ruleNo}", 1L)
                .header("X-USER", "USER")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.ruleName").value("수정된 룰"))
                .andExpect(jsonPath("$.ruleDescription").value("수정 설명"))
                .andDo(print());
    }

    @Test
    @DisplayName("룰 삭제 테스트")
    void deleteRule() throws Exception {
        mockMvc.perform(delete("/api/v1/rules/{ruleNo}", 1L)
                .header("X-USER", "USER"))
                .andExpect(status().isNoContent());

        verify(ruleService).deleteRule(1L);
    }

    @Test
    @DisplayName("존재하지 않는 규칙 조회 시 404 반환")
    void getRule_NotFound() throws Exception {
        // Given
        when(ruleService.getRule(999L)).thenThrow(new RuleNotFoundException(999L));

        // When & Then
        mockMvc.perform(get("/api/v1/rules/999")
                .header("X-USER", "USER"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("유효하지 않은 입력값으로 등록 시 400 반환")
    void registerRule_InvalidInput() throws Exception {
        RuleRegisterRequest invalidRequest = new RuleRegisterRequest(null, "", "a", -1);

        mockMvc.perform(post("/api/v1/rules")
                        .header("X-USER", "USER")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}