package com.nhnacademy.ruleengineservice.service.rule.impl;

import com.nhnacademy.ruleengineservice.adaptor.MemberAdaptor;
import com.nhnacademy.ruleengineservice.auth.MemberThreadLocal;
import com.nhnacademy.ruleengineservice.domain.rule.Rule;
import com.nhnacademy.ruleengineservice.domain.rule.RuleGroup;
import com.nhnacademy.ruleengineservice.domain.rule.RuleMemberMapping;
import com.nhnacademy.ruleengineservice.domain.trigger.TriggerEvent;
import com.nhnacademy.ruleengineservice.dto.member.MemberResponse;
import com.nhnacademy.ruleengineservice.dto.rule.RuleRegisterRequest;
import com.nhnacademy.ruleengineservice.dto.rule.RuleResponse;
import com.nhnacademy.ruleengineservice.dto.rule.RuleUpdateRequest;
import com.nhnacademy.ruleengineservice.exception.member.MemberNotFoundException;
import com.nhnacademy.ruleengineservice.exception.member.UnauthorizedException;
import com.nhnacademy.ruleengineservice.exception.rule.RuleGroupNotFoundException;
import com.nhnacademy.ruleengineservice.exception.rule.RuleNotFoundException;
import com.nhnacademy.ruleengineservice.exception.rule.RulePersistException;
import com.nhnacademy.ruleengineservice.repository.rule.RuleGroupRepository;
import com.nhnacademy.ruleengineservice.repository.rule.RuleMemberMappingRepository;
import com.nhnacademy.ruleengineservice.repository.rule.RuleRepository;
import com.nhnacademy.ruleengineservice.repository.trigger.TriggerRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@DataJpaTest
@ActiveProfiles("test")
class RuleServiceImplTest {

    @Mock
    private RuleGroupRepository ruleGroupRepository;

    @Mock
    private RuleRepository ruleRepository;

    @Mock
    private RuleMemberMappingRepository ruleMemberMappingRepository;

    @Mock
    private MemberAdaptor memberAdaptor;

    @Mock
    private TriggerRepository triggerRepository;

    @InjectMocks
    private RuleServiceImpl ruleService;

    @Test
    @DisplayName("규칙 등록 성공")
    void registerRule() {
        String email = "user@test.com";
        MemberThreadLocal.setMemberEmail(email);

        Long ruleGroupNo = 1L;
        String ruleName = "test rule";
        String ruleDescription = "test description";
        Integer rulePriority = 1;

        RuleRegisterRequest request = new RuleRegisterRequest(
                ruleGroupNo, ruleName, ruleDescription, rulePriority
        );

        RuleGroup group = RuleGroup.ofNewRuleGroup("test g", "des d", 2);
        setField(group, "ruleGroupNo", 1L);

        Rule rule = Rule.ofNewRule(group, ruleName, ruleDescription, rulePriority);
        setField(rule, "ruleNo", 10L);

        TriggerEvent event = TriggerEvent.ofNewTriggerEvent(
                rule,
                "AI_DATA_RECEIVED",
                "{\"source\":\"AI\"}"
        );

        MemberResponse memberResponse = new MemberResponse(
                2L,
                "ROLE_USER",
                "user",
                email,
                "pass",
                "010-1234-5678"
        );

        RuleMemberMapping ruleMemberMapping = RuleMemberMapping.ofNewRuleMemberMapping(
                rule,
                memberResponse.getNo()
        );

        when(ruleGroupRepository.findById(ruleGroupNo)).thenReturn(Optional.of(group));
        when(ruleRepository.save(Mockito.any())).thenReturn(rule);
        when(triggerRepository.save(Mockito.any())).thenReturn(event);
        when(memberAdaptor.getMemberByEmail(anyString())).thenReturn(ResponseEntity.ok(memberResponse));

        when(ruleMemberMappingRepository.save(Mockito.any())).thenReturn(ruleMemberMapping);

        RuleResponse response = ruleService.registerRule(request);
        log.debug("register Rule : {}", response);

        assertNotNull(response);
        assertEquals(email, memberResponse.getEmail());
        assertAll(
                () -> assertEquals(10L, response.getRuleNo()),
                () -> assertEquals(ruleName, response.getRuleName()),
                () -> assertEquals(ruleDescription, response.getRuleDescription()),
                () -> assertEquals(rulePriority, response.getRulePriority()),
                () -> assertEquals(1L, response.getRuleGroupNo())
        );

        verify(ruleGroupRepository, Mockito.times(1)).findById(Mockito.anyLong());
        verify(memberAdaptor).getMemberByEmail(email);
        verify(ruleRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    @DisplayName("규칙 등록 실패 - MemberThreadLocal 이메일 없음")
    void registerRule_missingEmail() {
        MemberThreadLocal.removedMemberEmail(); // 이메일 클리어
        RuleRegisterRequest request = new RuleRegisterRequest(1L, "Rule", "Desc", 1);

        assertThrows(UnauthorizedException.class, () -> ruleService.registerRule(request));
    }

    @Test
    @DisplayName("규칙 등록 실패 - MemberAdaptor 404 응답")
    void registerRule_memberNotFound() {
        String email = "invalid@test.com";
        MemberThreadLocal.setMemberEmail(email);
        RuleRegisterRequest request = new RuleRegisterRequest(1L, "Rule", "Desc", 1);

        when(ruleGroupRepository.findById(any()))
                .thenReturn(Optional.of(RuleGroup.ofNewRuleGroup("Group", "Desc", 1)));
        when(memberAdaptor.getMemberByEmail(email))
                .thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).build());

        assertThrows(MemberNotFoundException.class, () -> ruleService.registerRule(request));
    }

    @Test
    @DisplayName("규칙 등록 실패 - 존재하지 않는 RuleGroup")
    void registerRule_exception() {
        String email = "test@test.com";
        MemberThreadLocal.setMemberEmail(email);

        Long nonGroupNo = 2314L;

        RuleRegisterRequest request = new RuleRegisterRequest(nonGroupNo, "테스트 그룹", "설명", 1);

        when(ruleGroupRepository.findById(nonGroupNo)).thenReturn(Optional.empty());

        assertThrows(RuleGroupNotFoundException.class, () -> ruleService.registerRule(request));

        verify(ruleRepository, never()).save(Mockito.any());
    }

    @Test
    @DisplayName("규칙 등록 실패 - MemberAdaptor 응답 null")
    void registerRule_memberAdaptorReturnsNull() {
        String email = "abc@test.com";
        MemberThreadLocal.setMemberEmail(email);

        RuleRegisterRequest request = new RuleRegisterRequest(1L, "Rule", "Desc", 1);
        RuleGroup mockGroup = RuleGroup.ofNewRuleGroup("Group", "Desc", 1);

        when(ruleGroupRepository.findById(anyLong())).thenReturn(Optional.of(mockGroup));
        when(memberAdaptor.getMemberByEmail(anyString())).thenReturn(null);

        assertThrows(MemberNotFoundException.class, () -> ruleService.registerRule(request));
    }

    @Test
    @DisplayName("규칙 등록 실패 - 매핑 저장 실패")
    void registerRule_mappingSaveFailed() {
        String email = "user@test.com";
        MemberThreadLocal.setMemberEmail(email);

        RuleRegisterRequest request = new RuleRegisterRequest(1L, "Rule", "Desc", 1);

        when(ruleGroupRepository.findById(any()))
                .thenReturn(Optional.of(RuleGroup.ofNewRuleGroup("Group", "Desc", 1)));
        when(memberAdaptor.getMemberByEmail(email))
                .thenReturn(ResponseEntity.ok(new MemberResponse(
                        1L,
                        "falseRule",
                        "valid name",
                        "user@test.com",
                        "1234asdf!",
                        "010-1234-6343"
                )));

        when(ruleMemberMappingRepository.save(any()))
                .thenThrow(new DataAccessException("DB Error") {});

        assertThrows(RulePersistException.class, () -> ruleService.registerRule(request));
    }

    @Test
    @DisplayName("규칙 수정 성공")
    void updateRule() {
        Long ruleNo = 1L;
        String updatedName = "수정된 규칙";
        String updatedDescription = "수정된 설명";
        Integer updatedPriority = 2;

        RuleUpdateRequest request = new RuleUpdateRequest(updatedName, updatedDescription, updatedPriority);

        RuleGroup mockRuleGroup = RuleGroup.ofNewRuleGroup("그룹 이름", "그룹 설명", 1);
        setField(mockRuleGroup, "ruleGroupNo", 1L);

        Rule existingRule = Rule.ofNewRule(mockRuleGroup, "기존 규칙", "기존 설명", 3);
        setField(existingRule, "ruleNo", ruleNo);

        when(ruleRepository.findById(ruleNo)).thenReturn(Optional.of(existingRule));

        RuleResponse response = ruleService.updateRule(ruleNo, request);
        log.debug("updated rule : {}", response);

        assertNotNull(response);
        assertAll(
                () -> assertEquals(ruleNo, response.getRuleNo()),
                () -> assertEquals(updatedName, response.getRuleName()),
                () -> assertEquals(updatedDescription, response.getRuleDescription()),
                () -> assertEquals(updatedPriority, response.getRulePriority())
        );

        verify(ruleRepository).findById(ruleNo);
    }

    @Test
    @DisplayName("규칙 수정 실패")
    void updateRule_exception() {
        Long nonExistentRuleNo = 999L;
        RuleUpdateRequest request = new RuleUpdateRequest("수정 규칙", "설명", 1);

        when(ruleRepository.findById(nonExistentRuleNo)).thenReturn(Optional.empty());

        assertThrows(RuleNotFoundException.class, () -> ruleService.updateRule(nonExistentRuleNo, request));
    }

    @Test
    @DisplayName("규칙 삭제 성공")
    void deleteRule() {
        Long ruleNo = 1L;
        when(ruleRepository.existsById(ruleNo)).thenReturn(true);

        ruleService.deleteRule(ruleNo);

        verify(ruleRepository).existsById(ruleNo);
        verify(ruleRepository).deleteById(ruleNo);
    }

    @Test
    @DisplayName("규칙 삭제 실패")
    void deleteRule_exception() {
        Long nonExistentRuleNo = 999L;
        when(ruleRepository.existsById(nonExistentRuleNo)).thenReturn(false);

        assertThrows(RuleNotFoundException.class, () -> ruleService.deleteRule(nonExistentRuleNo));

        verify(ruleRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("규칙 단건 조회 성공")
    void getRule() {
        Long ruleNo = 1L;

        RuleGroup mockRuleGroup = RuleGroup.ofNewRuleGroup("rule group", "g d", 1);
        setField(mockRuleGroup, "ruleGroupNo", 1L);

        Rule mockRule = Rule.ofNewRule(mockRuleGroup, "테스트 규칙", "테스트 설명", 1);
        setField(mockRule, "ruleNo", ruleNo);

        when(ruleRepository.findById(ruleNo)).thenReturn(Optional.of(mockRule));

        // when
        RuleResponse response = ruleService.getRule(ruleNo);

        // then
        assertNotNull(response);
        assertAll(
                () -> assertEquals(ruleNo, response.getRuleNo()),
                () -> assertEquals("테스트 규칙", response.getRuleName()),
                () -> assertEquals("테스트 설명", response.getRuleDescription()),
                () -> assertEquals(1, response.getRulePriority())
        );

        verify(ruleRepository).findById(ruleNo);
    }

    @Test
    @DisplayName("규칙 단건 조회 실패")
    void getRule_withNonExistentRule_throwsException() {
        Long nonExistentRuleNo = 999L;
        when(ruleRepository.findById(nonExistentRuleNo)).thenReturn(Optional.empty());

        assertThrows(RuleNotFoundException.class, () -> ruleService.getRule(nonExistentRuleNo));
    }

    @Test
    @DisplayName("모든 규칙 목록 조회 성공")
    void getAllRule() {
        List<Rule> mockRules = new ArrayList<>();

        RuleGroup mockRuleGroup = RuleGroup.ofNewRuleGroup("test GG", "description", 1);
        setField(mockRuleGroup, "ruleGroupNo", 1L);

        Rule rule1 = Rule.ofNewRule(mockRuleGroup, "규칙1", "셜명1", 2);
        setField(rule1, "ruleNo", 2L);

        Rule rule2 = Rule.ofNewRule(mockRuleGroup, "규칙2", "셜명2", 3);
        setField(rule2, "ruleNo", 3L);

        mockRules.add(rule1);
        mockRules.add(rule2);

        when(ruleRepository.findAll()).thenReturn(mockRules);

        // when
        List<RuleResponse> responses = ruleService.getAllRule();

        // then
        assertNotNull(responses);
        assertAll(
                () -> assertEquals(2, responses.size()),
                () -> assertEquals("규칙1", responses.get(0).getRuleName()),
                () -> assertEquals("규칙2", responses.get(1).getRuleName())
        );
    }

    @Test
    @DisplayName("모든 규칙 목록 조회 실패")
    void getAllRule_exception() {
        when(ruleRepository.findAll()).thenReturn(new ArrayList<>());

        assertThrows(RuleNotFoundException.class, () -> ruleService.getAllRule());
    }

    @Test
    @DisplayName("그룹별 규칙 목록 조회 성공")
    void getRulesByGroup() {
        Long ruleGroupNo = 1L;

        RuleGroup mockRuleGroup = RuleGroup.ofNewRuleGroup("test GG", "description", 1);
        setField(mockRuleGroup, "ruleGroupNo", 1L);

        List<Rule> mockRules = new ArrayList<>();
        Rule rule1 = Rule.ofNewRule(mockRuleGroup, "그룹 규칙1", "설명", 1);
        setField(rule1, "ruleNo", 10L);

        Rule rule2 = Rule.ofNewRule(mockRuleGroup, "그룹 규칙2", "설명", 2);
        setField(rule2, "ruleNo", 20L);

        mockRules.add(rule1);
        mockRules.add(rule2);

        when(ruleGroupRepository.findById(ruleGroupNo)).thenReturn(Optional.of(mockRuleGroup));
        when(ruleRepository.findByRuleGroup(mockRuleGroup)).thenReturn(mockRules);

        List<RuleResponse> responses = ruleService.getRulesByGroup(ruleGroupNo);

        assertNotNull(responses);
        assertAll(
                () -> assertEquals(2, responses.size()),
                () -> assertEquals("그룹 규칙1", responses.get(0).getRuleName()),
                () -> assertEquals("그룹 규칙2", responses.get(1).getRuleName())
        );
    }

    @Test
    @DisplayName("그룹별 목록 조회 실패")
    void getRulesByGroup_exception() {
        Long nonExistentGroupNo = 999L;
        when(ruleGroupRepository.findById(nonExistentGroupNo)).thenReturn(Optional.empty());

        assertThrows(RuleGroupNotFoundException.class, () -> ruleService.getRulesByGroup(nonExistentGroupNo));
    }

    @Test
    @DisplayName("규칙 활성화 상태 변경 성공")
    void setRuleActive() {
        Long ruleNo = 1L;
        boolean newActiveStatus = true;

        RuleGroup mockRuleGroup = RuleGroup.ofNewRuleGroup("test Group", "description", 1);
        setField(mockRuleGroup, "ruleGroupNo", 1L);

        Rule rule = Rule.ofNewRule(mockRuleGroup, "테스트 규칙", "설명", 1);
        setField(rule, "ruleNo", 30L);
        rule.setActive(false);

        when(ruleRepository.findById(ruleNo)).thenReturn(Optional.of(rule));
        when(ruleRepository.save(any(Rule.class))).thenReturn(rule);

        // when
        ruleService.setRuleActive(ruleNo, newActiveStatus);

        // then
        assertTrue(rule.isActive()); // 상태가 변경되었는지 확인
        verify(ruleRepository).findById(ruleNo);
        verify(ruleRepository).save(rule);
    }

    @Test
    @DisplayName("규칙 활성화 상태 변경 실패")
    void setRuleActive_exception() {
        Long nonExistentRuleNo = 999L;
        when(ruleRepository.findById(nonExistentRuleNo)).thenReturn(Optional.empty());

        assertThrows(RuleNotFoundException.class, () -> ruleService.setRuleActive(nonExistentRuleNo, true));

        verify(ruleRepository, never()).save(any(Rule.class));
    }

    @Test
    @DisplayName("룰 엔티티 검증")
    void getRuleEntity() {
        Long ruleNo = 1L;
        RuleGroup ruleGroup = Mockito.mock();
        Rule rule = Rule.ofNewRule(ruleGroup, "엔티티 검증", "설명",1);
        setField(rule, "ruleNo", 1L);

        when(ruleRepository.findById(ruleNo)).thenReturn(Optional.of(rule));

        Rule result = ruleService.getRuleEntity(ruleNo);

        assertNotNull(result);
        assertAll(
                () -> assertEquals(ruleNo, result.getRuleNo()),
                () -> assertEquals("엔티티 검증", result.getRuleName()),
                () -> assertEquals("설명", result.getRuleDescription()),
                () -> assertEquals(1, result.getRulePriority())
        );
    }

    @Test
    @DisplayName("존재하지 않는 룰 엔티티 조회 시 예외 발생")
    void getRuleEntity_notFound_throwsException() {
        Long nonExistentRuleNo = 999L;
        when(ruleRepository.findById(nonExistentRuleNo)).thenReturn(Optional.empty());

        assertThrows(RuleNotFoundException.class, () -> ruleService.getRuleEntity(nonExistentRuleNo));

        verify(ruleRepository).findById(nonExistentRuleNo);
    }

    private void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}