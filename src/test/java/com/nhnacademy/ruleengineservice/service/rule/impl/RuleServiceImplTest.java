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
import com.nhnacademy.ruleengineservice.exception.rule.RuleGroupNotFoundException;
import com.nhnacademy.ruleengineservice.exception.rule.RuleNotFoundException;
import com.nhnacademy.ruleengineservice.exception.rule.RulePersistException;
import com.nhnacademy.ruleengineservice.repository.rule.RuleGroupRepository;
import com.nhnacademy.ruleengineservice.repository.rule.RuleMemberMappingRepository;
import com.nhnacademy.ruleengineservice.repository.rule.RuleRepository;
import com.nhnacademy.ruleengineservice.repository.trigger.TriggerRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataAccessException;
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

    private final String testEmail = "user@test.com";
    private final Long testMemberNo = 1L;

    @BeforeEach
    void setUp() {
        MemberThreadLocal.setMemberEmail(testEmail);
    }

    @Test
    @DisplayName("규칙 등록 성공")
    void registerRule() {
        RuleRegisterRequest request = new RuleRegisterRequest(1L, "Test Rule", "Description", 1);
        RuleGroup mockRuleGroup = mock();
        Rule mockRule = mock();
        TriggerEvent mockTrigger = mock();
        MemberResponse mockMemberResponse = new MemberResponse(
                testMemberNo,
                "test rule",
                testEmail,
                "Test User",
                "asdf1234!",
                "010-1234-5678"
        );

        when(mockRuleGroup.getRuleGroupNo()).thenReturn(1L);
        when(mockRule.getRuleGroup()).thenReturn(mockRuleGroup);

        when(ruleGroupRepository.findById(1L)).thenReturn(Optional.of(mockRuleGroup));
        when(memberAdaptor.getMemberByEmail(testEmail)).thenReturn(ResponseEntity.ok(mockMemberResponse));
        when(ruleRepository.save(any(Rule.class))).thenReturn(mockRule);
        when(triggerRepository.save(any(TriggerEvent.class))).thenReturn(mockTrigger);
        when(ruleMemberMappingRepository.save(any(RuleMemberMapping.class))).thenReturn(mock(RuleMemberMapping.class));

        RuleResponse result = ruleService.registerRule(request);

        assertNotNull(result);
        verify(ruleGroupRepository).findById(1L);
        verify(memberAdaptor).getMemberByEmail(testEmail);
        verify(ruleRepository, times(2)).save(any(Rule.class));
        verify(triggerRepository).save(any(TriggerEvent.class));
        verify(ruleMemberMappingRepository).save(any(RuleMemberMapping.class));
    }

    @Test
    @DisplayName("룰 저장 - 룰 그룹 찾을 수 없음")
    void registerRule_RuleGroupNotFound() {
        RuleRegisterRequest request = new RuleRegisterRequest(999L, "Invalid Rule", "Desc", 1);
        when(ruleGroupRepository.findById(999L)).thenReturn(java.util.Optional.empty());

        assertThrows(RuleGroupNotFoundException.class, () -> ruleService.registerRule(request));
    }

    @Test
    @DisplayName("룰 저장 - 맴버 찾을 수 없음")
    void registerRule_MemberNotFound() {
        RuleRegisterRequest request = new RuleRegisterRequest(1L, "Test Rule", "Desc", 1);
        RuleGroup mockRuleGroup = mock(RuleGroup.class);
        when(ruleGroupRepository.findById(1L)).thenReturn(java.util.Optional.of(mockRuleGroup));
        when(memberAdaptor.getMemberByEmail(testEmail)).thenReturn(ResponseEntity.notFound().build());

        assertThrows(MemberNotFoundException.class, () -> ruleService.registerRule(request));
    }

    @Test
    @DisplayName("룰 저장 - 매핑 실패")
    void registerRule_MappingFailure() {
        RuleRegisterRequest request = new RuleRegisterRequest(1L, "Test Rule", "Desc", 1);
        RuleGroup mockRuleGroup = mock(RuleGroup.class);
        MemberResponse mockMemberResponse = new MemberResponse(
                testMemberNo,
                "test rule",
                testEmail,
                "Test User",
                "asdf1234!",
                "010-1234-5678"
        );
        Rule mockRule = mock(Rule.class);

        when(ruleGroupRepository.findById(1L)).thenReturn(java.util.Optional.of(mockRuleGroup));
        when(memberAdaptor.getMemberByEmail(testEmail)).thenReturn(ResponseEntity.ok(mockMemberResponse));
        when(ruleRepository.save(any(Rule.class))).thenReturn(mockRule);
        when(triggerRepository.save(any(TriggerEvent.class))).thenReturn(mock(TriggerEvent.class));
        when(ruleMemberMappingRepository.save(any(RuleMemberMapping.class)))
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