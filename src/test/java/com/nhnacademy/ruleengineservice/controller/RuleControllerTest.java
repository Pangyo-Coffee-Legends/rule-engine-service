package com.nhnacademy.ruleengineservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.ruleengineservice.dto.rule.RuleRegisterRequest;
import com.nhnacademy.ruleengineservice.dto.rule.RuleResponse;
import com.nhnacademy.ruleengineservice.dto.rule.RuleUpdateRequest;
import com.nhnacademy.ruleengineservice.service.rule.RuleService;
import lombok.extern.slf4j.Slf4j;
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

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@ActiveProfiles("test")
@WebMvcTest(RuleController.class)
@AutoConfigureMockMvc(addFilters = false) // Security Filter 비활성화
class RuleControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    RuleService ruleService;

    @Test
    @DisplayName("룰 등록 테스트")
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

        Mockito.when(ruleService.registerRule(Mockito.any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/rules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
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

        Mockito.when(ruleService.getRule(Mockito.anyLong())).thenReturn(response);

        mockMvc.perform(get("/api/v1/rules/{ruleNo}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ruleNo").value(1))
                .andExpect(jsonPath("$.ruleName").value("테스트 룰"))
                .andDo(print());
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

        Mockito.when(ruleService.getAllRule()).thenReturn(responseList);

        mockMvc.perform(get("/api/v1/rules"))
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

        Mockito.when(ruleService.updateRule(Mockito.anyLong(), Mockito.any())).thenReturn(response);

        mockMvc.perform(put("/api/v1/rules/{ruleNo}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ruleName").value("수정된 룰"))
                .andExpect(jsonPath("$.ruleDescription").value("수정 설명"))
                .andDo(print());
    }

    @Test
    @DisplayName("룰 삭제 테스트")
    void deleteRule() throws Exception {
        mockMvc.perform(delete("/api/v1/rules/{ruleNo}", 1L))
                .andExpect(status().isNoContent());

        Mockito.verify(ruleService).deleteRule(1L);
    }
}