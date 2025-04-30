package com.nhnacademy.ruleengineservice.service.condition.impl;

import com.nhnacademy.ruleengineservice.domain.condition.Condition;
import com.nhnacademy.ruleengineservice.domain.rule.Rule;
import com.nhnacademy.ruleengineservice.domain.rule.RuleGroup;
import com.nhnacademy.ruleengineservice.dto.condition.ConditionRegisterRequest;
import com.nhnacademy.ruleengineservice.dto.condition.ConditionResponse;
import com.nhnacademy.ruleengineservice.exception.condition.ConditionNotFoundException;
import com.nhnacademy.ruleengineservice.exception.rule.RuleNotFoundException;
import com.nhnacademy.ruleengineservice.repository.condition.ConditionRepository;
import com.nhnacademy.ruleengineservice.repository.rule.RuleRepository;
import com.nhnacademy.ruleengineservice.service.rule.impl.RuleServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Slf4j
@DataJpaTest
@ActiveProfiles("test")
class ConditionServiceImplTest {

    @Mock
    private ConditionRepository conditionRepository;

    @Mock
    private RuleServiceImpl ruleService;

    @InjectMocks
    private ConditionServiceImpl conditionService;

    @Test
    @DisplayName("조건 등록 성공")
    void registerCondition() {
        Long ruleNo = 1L;
        String conType = "EQ";
        String conField = "temperature";
        String conValue = "25";
        Integer conPriority = 1;

        ConditionRegisterRequest request = new ConditionRegisterRequest(
                ruleNo, conType, conField, conValue, conPriority);

        RuleGroup ruleGroup = RuleGroup.ofNewRuleGroup("ex1", "ex1 de", 1);

        Rule mockRule = Rule.ofNewRule(ruleGroup, "ru1", "de1", 1);
        setField(mockRule, "ruleNo", 1L);

        Condition savedCondition = Condition.ofNewCondition(mockRule, conType, conField, conValue, conPriority);
        setField(savedCondition, "conditionNo", 1L);

        when(ruleService.getRuleEntity(ruleNo)).thenReturn(mockRule);
        when(conditionRepository.save(Mockito.any(Condition.class))).thenReturn(savedCondition);

        ConditionResponse response = conditionService.registerCondition(request);
        log.debug("register condition : {}", response);

        assertNotNull(response);
        assertAll(
                () -> assertEquals(1L, response.getConditionNo()),
                () -> assertEquals(ruleNo, response.getRuleNo()),
                () -> assertEquals(conType, response.getConType())
        );

        verify(ruleService).getRuleEntity(ruleNo);
        verify(conditionRepository).save(Mockito.any());
    }

    @Test
    @DisplayName("조건 등록 실패")
    void registerCondition_exception() {
        Long nonExistentRuleNo = 1365L;
        ConditionRegisterRequest request = new ConditionRegisterRequest(
                nonExistentRuleNo, "EQ", "field", "value", 1);

        when(ruleService.getRuleEntity(nonExistentRuleNo))
                .thenThrow(new RuleNotFoundException(nonExistentRuleNo));

        assertThrows(RuleNotFoundException.class, () -> {
            conditionService.registerCondition(request);
        });

        verify(conditionRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    @DisplayName("조건 삭제 성공")
    void deleteCondition() {
        Long conditionNo = 1L;
        when(conditionRepository.existsById(conditionNo)).thenReturn(true);

        conditionService.deleteCondition(conditionNo);

        verify(conditionRepository).existsById(conditionNo);
        verify(conditionRepository).deleteById(conditionNo);
    }

    @Test
    @DisplayName("조건 삭제 실패")
    void deleteCondition_withNonExistentCondition_throwsException() {
        Long nonExistentConditionNo = 999L;
        when(conditionRepository.existsById(nonExistentConditionNo)).thenReturn(false);

        assertThrows(ConditionNotFoundException.class, () -> {
            conditionService.deleteCondition(nonExistentConditionNo);
        });
        verify(conditionRepository, Mockito.never()).deleteById(Mockito.anyLong());
    }

    @Test
    @DisplayName("조건 단건 조회 성공")
    void getCondition() {
        Long conditionNo = 1L;
        RuleGroup ruleGroup = RuleGroup.ofNewRuleGroup("임시", "설명", 1);

        Rule rule = Rule.ofNewRule(ruleGroup, "rule", "설명", 1);
        setField(rule, "ruleNo", 100L);

        Condition mockCondition = Condition.ofNewCondition(
                rule,
                "EQ",
                "temperature",
                "25",
                1
        );
        setField(mockCondition, "conditionNo", conditionNo);

        when(conditionRepository.findById(conditionNo)).thenReturn(Optional.of(mockCondition));

        ConditionResponse response = conditionService.getCondition(conditionNo);

        assertNotNull(response);
        assertAll(
                () -> assertEquals(conditionNo, response.getConditionNo()),
                () -> assertEquals(rule.getRuleNo(), response.getRuleNo()),
                () -> assertEquals("EQ", response.getConType()),
                () -> assertEquals("temperature", response.getConField()),
                () -> assertEquals("25", response.getConValue()),
                () -> assertEquals(1, response.getConPriority())
        );
    }
    // 실패 추가
    @Test
    @DisplayName("조건 단건 조회 실패")
    void getCondition_exception() {
        Long nonExistentConditionNo = 1235L;
        when(conditionRepository.findById(nonExistentConditionNo)).thenReturn(Optional.empty());

        assertThrows(ConditionNotFoundException.class, () -> {
            conditionService.getCondition(nonExistentConditionNo);
        });
    }

    @Test
    @DisplayName("규칙별 조건 목록 조회 성공")
    void getConditionsByRule() {
        List<Condition> mockConditions = new ArrayList<>();
        RuleGroup ruleGroup = RuleGroup.ofNewRuleGroup("임시", "설명", 1);
        Rule rule = Rule.ofNewRule(ruleGroup, "rule", "설명", 1);

        Condition condition1 = Condition.ofNewCondition(rule, "EQ", "field1", "21", 1);
        Condition condition2 = Condition.ofNewCondition(rule, "GT", "field2", "30", 1);

        mockConditions.add(condition1);
        mockConditions.add(condition2);

        when(conditionRepository.findAll()).thenReturn(mockConditions);

        // when
        List<ConditionResponse> responses = conditionService.getConditionsByRule(1L);

        // then
        assertNotNull(responses);
        assertEquals(2, responses.size());
    }

    @Test
    @DisplayName("조건 평가 - EQ 성공")
    void evaluateCondition_EQ_success() {
        Long conditionNo = 1L;
        Map<String, Object> facts = new HashMap<>();
        facts.put("temperature", "25");

        RuleGroup ruleGroup = RuleGroup.ofNewRuleGroup("임시", "설명", 1);
        Rule rule = Rule.ofNewRule(ruleGroup, "rule", "설명", 1);

        Condition condition = Condition.ofNewCondition(rule, "EQ", "temperature", "25", 1);

        when(conditionRepository.findById(conditionNo)).thenReturn(Optional.of(condition));

        boolean result = conditionService.evaluateCondition(conditionNo, facts);

        assertTrue(result);
    }

    @Test
    @DisplayName("조건 평가 - GT 성공")
    void evaluateCondition_GT_success() {
        Long conditionNo = 1L;
        Map<String, Object> facts = new HashMap<>();
        facts.put("temperature", "30");

        RuleGroup ruleGroup = RuleGroup.ofNewRuleGroup("임시", "설명", 1);
        Rule rule = Rule.ofNewRule(ruleGroup, "rule", "설명", 1);

        Condition condition = Condition.ofNewCondition(rule, "GT", "temperature", "25", 1);

        when(conditionRepository.findById(conditionNo)).thenReturn(Optional.of(condition));

        boolean result = conditionService.evaluateCondition(conditionNo, facts);

        assertTrue(result);
    }

    @Test
    @DisplayName("조건 평가 - LIKE 성공")
    void evaluateCondition_LIKE_success() {
        // given
        Long conditionNo = 1L;
        Map<String, Object> facts = new HashMap<>();
        facts.put("name", "Kim Monsoon");

        RuleGroup ruleGroup = RuleGroup.ofNewRuleGroup("임시", "설명", 1);
        Rule rule = Rule.ofNewRule(ruleGroup, "rule", "설명", 1);

        Condition condition = Condition.ofNewCondition(rule, "LIKE", "name", "Kim.*", 1);

        when(conditionRepository.findById(conditionNo)).thenReturn(Optional.of(condition));

        boolean result = conditionService.evaluateCondition(conditionNo, facts);

        assertTrue(result);
    }

    @Test
    @DisplayName("조건 평가 - BETWEEN 성공")
    void evaluateCondition_BETWEEN_success() {
        // given
        Long conditionNo = 1L;
        Map<String, Object> facts = new HashMap<>();
        facts.put("temperature", "25");

        RuleGroup ruleGroup = RuleGroup.ofNewRuleGroup("임시", "설명", 1);
        Rule rule = Rule.ofNewRule(ruleGroup, "rule", "설명", 1);

        Condition condition = Condition.ofNewCondition(rule, "BETWEEN", "temperature", "20, 30", 1);

        when(conditionRepository.findById(conditionNo)).thenReturn(Optional.of(condition));

        boolean result = conditionService.evaluateCondition(conditionNo, facts);

        assertTrue(result);
    }

    @Test
    @DisplayName("필드가 존재하지 않는 조건 평가 시 false 반환")
    void evaluateCondition_withNonExistentField_returnsFalse() {
        Long conditionNo = 1L;
        Map<String, Object> facts = new HashMap<>();
        // facts 에 "temperature" 필드가 없음

        RuleGroup ruleGroup = RuleGroup.ofNewRuleGroup("임시", "설명", 1);
        Rule rule = Rule.ofNewRule(ruleGroup, "rule", "설명", 1);

        Condition condition = Condition.ofNewCondition(rule, "EQ", "temperature", "25", 1);

        when(conditionRepository.findById(conditionNo)).thenReturn(Optional.of(condition));

        boolean result = conditionService.evaluateCondition(conditionNo, facts);

        assertFalse(result);
    }

    @Test
    @DisplayName("조건 엔티티 검증")
    void getConditionEntity() {
        Long conNo = 1L;

        Rule rule = Mockito.mock();
        Condition condition = Condition.ofNewCondition(rule, "EQ", "temperature", "25",1);
        setField(condition, "conditionNo", conNo);

        when(conditionRepository.findById(conNo)).thenReturn(Optional.of(condition));

        Condition result = conditionService.getConditionEntity(conNo);

        assertNotNull(result);
        assertAll(
                () -> assertEquals(conNo, result.getConditionNo()),
                () -> assertEquals("EQ", result.getConType()),
                () -> assertEquals("temperature", result.getConField()),
                () -> assertEquals("25", result.getConValue()),
                () -> assertEquals(1, result.getConPriority())
        );
    }

    @Test
    @DisplayName("존재하지 않는 조건 엔티티 조회 시 예외 발생")
    void getConditionEntity_exception() {
        Long nonExistentConditionNo = 9126L;
        when(conditionRepository.findById(nonExistentConditionNo)).thenReturn(Optional.empty());

        assertThrows(ConditionNotFoundException.class, () -> {
            conditionService.getConditionEntity(nonExistentConditionNo);
        });

        verify(conditionRepository).findById(nonExistentConditionNo);
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