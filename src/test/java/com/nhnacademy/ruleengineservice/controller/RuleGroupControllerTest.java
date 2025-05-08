package com.nhnacademy.ruleengineservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.ruleengineservice.dto.rule.RuleGroupRegisterRequest;
import com.nhnacademy.ruleengineservice.dto.rule.RuleGroupResponse;
import com.nhnacademy.ruleengineservice.service.rule.RuleGroupService;
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

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(RuleGroupController.class)
@AutoConfigureMockMvc(addFilters = false)
class RuleGroupControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    RuleGroupService ruleGroupService;

    @Test
    @DisplayName("새 규칙 그룹 등록 테스트")
    void registerRuleGroup() throws Exception {
        RuleGroupRegisterRequest request = new RuleGroupRegisterRequest(
                "테스트 그룹", "설명", 1);

        RuleGroupResponse response = new RuleGroupResponse(
                1L, "테스트 그룹", "설명", 1, true);

        Mockito.when(ruleGroupService.registerRuleGroup(Mockito.any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/rule-groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ruleGroupNo").value(1))
                .andExpect(jsonPath("$.ruleGroupName").value("테스트 그룹"))
                .andExpect(jsonPath("$.ruleGroupDescription").value("설명"))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.priority").value(1))
                .andDo(print());

        Mockito.verify(ruleGroupService).registerRuleGroup(Mockito.any());
    }

    @Test
    @DisplayName("단일 규칙 그룹 조회 테스트")
    void getRuleGroup() throws Exception {
        RuleGroupResponse response = new RuleGroupResponse(
                1L, "테스트 그룹", "설명", 1, true);

        Mockito.when(ruleGroupService.getRuleGroup(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/rule-groups/{ruleGroupNo}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ruleGroupNo").value(1))
                .andExpect(jsonPath("$.ruleGroupName").value("테스트 그룹"))
                .andDo(print());

        Mockito.verify(ruleGroupService).getRuleGroup(1L);
    }

    @Test
    @DisplayName("모든 그룹 조회 테스트")
    void getRuleGroups() throws Exception {
        List<RuleGroupResponse> responseList = List.of(
                new RuleGroupResponse(1L, "테스트 그룹1", "설명1", 1, true),
                new RuleGroupResponse(2L, "테스트 그룹2", "설명2", 2, true)
        );

        Mockito.when(ruleGroupService.getAllRuleGroups()).thenReturn(responseList);

        mockMvc.perform(get("/api/v1/rule-groups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ruleGroupNo").value(1))
                .andExpect(jsonPath("$[1].ruleGroupNo").value(2))
                .andExpect(jsonPath("$[0].ruleGroupName").value("테스트 그룹1"))
                .andExpect(jsonPath("$[1].ruleGroupName").value("테스트 그룹2"))
                .andDo(print());

        Mockito.verify(ruleGroupService).getAllRuleGroups();
    }

    @Test
    @DisplayName("규칙 그룹 삭제 테스트")
    void deleteRuleGroup() throws Exception {
        mockMvc.perform(delete("/api/v1/rule-groups/{ruleGroupNo}", 1L))
                .andExpect(status().isNoContent())
                .andDo(print());

        Mockito.verify(ruleGroupService).deleteRuleGroup(1L);
    }
}