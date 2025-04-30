package com.nhnacademy.ruleengineservice.service.engine;

import com.nhnacademy.ruleengineservice.domain.action.Action;
import com.nhnacademy.ruleengineservice.domain.condition.Condition;
import com.nhnacademy.ruleengineservice.domain.rule.Rule;
import com.nhnacademy.ruleengineservice.domain.rule.RuleGroup;
import com.nhnacademy.ruleengineservice.domain.trigger.TriggerEvent;
import com.nhnacademy.ruleengineservice.dto.action.ActionResult;
import com.nhnacademy.ruleengineservice.dto.condition.ConditionResult;
import com.nhnacademy.ruleengineservice.dto.engine.RuleEvaluationResult;
import com.nhnacademy.ruleengineservice.exception.rule.RuleNotFoundException;
import com.nhnacademy.ruleengineservice.repository.action.ActionRepository;
import com.nhnacademy.ruleengineservice.repository.condition.ConditionRepository;
import com.nhnacademy.ruleengineservice.repository.rule.RuleRepository;
import com.nhnacademy.ruleengineservice.repository.trigger.TriggerRepository;
import com.nhnacademy.ruleengineservice.service.action.ActionService;
import com.nhnacademy.ruleengineservice.service.condition.ConditionService;
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
import static org.mockito.Mockito.*;

@Slf4j
@DataJpaTest
@ActiveProfiles("test")
class RuleEngineServiceTest {

    @Mock
    private TriggerRepository triggerRepository;

    @Mock
    private RuleServiceImpl ruleService;

    @Mock
    private ConditionService conditionService;

    @Mock
    private ActionService actionService;

    @InjectMocks
    private RuleEngineService ruleEngineService;

    @Test
    @DisplayName("트리거 이벤트에 따른 룰 실행 - 조건 충족 시 액션 실행")
    void executeTriggeredRules_withMatchingConditions_executesActions() {
        String eventType = "AI_DATA_RECEIVED";
        String eventParams = "{\"source\":\"LSTM\"}";

        // AI에서 받아온 데이터
        Map<String, Object> facts = new HashMap<>();
        facts.put("comfort", 50);
        facts.put("location", "1");
        facts.put("sensor-name", "ABC");

        // 룰 설정
        RuleGroup ruleGroup = Mockito.mock();
        Rule rule = Rule.ofNewRule(ruleGroup, "쾌적도 알림 룰","설명", 1);
        setField(rule,"ruleNo", 1L);

        // 트리거 설정
        TriggerEvent trigger = TriggerEvent.ofNewTriggerEvent(rule, eventType, eventParams);
        List<TriggerEvent> triggers = List.of(trigger);

        // 필수 필드 및 조건 평가 결과 설정
        List<String> requiredFields = List.of("comfort", "location");

        ConditionResult conditionResult = new ConditionResult(1L, "comfort", "LT", "60", true);
        List<ConditionResult> conditionResults = List.of(conditionResult);

        ActionResult actionResult = ActionResult.ofNewActionResult(
                1L, true, "NOTIFICATION", "쾌적도 알림 전송 성공", null);
        List<ActionResult> actionResults = List.of(actionResult);

        // Mock 설정
        when(triggerRepository.findByEventType(eventType)).thenReturn(triggers);
        when(conditionService.getRequiredFieldsByRule(rule)).thenReturn(requiredFields);
        when(conditionService.evaluateConditionsForRule(rule, facts)).thenReturn(conditionResults);
        when(actionService.executeActionsForRule(eq(rule), any())).thenReturn(actionResults);

        List<RuleEvaluationResult> results = ruleEngineService.executeTriggeredRules(
                eventType, eventParams, facts);

        assertNotNull(results);
        assertEquals(1, results.size());
        RuleEvaluationResult result = results.get(0);
        assertTrue(result.isSuccess());
        assertEquals("룰 조건 충족, 액션 실행 완료", result.getMessage());
        assertEquals(1, result.getExecutedActions().size());
        assertEquals("NOTIFICATION", result.getExecutedActions().get(0).getActType());

        verify(triggerRepository).findByEventType(eventType);
        verify(conditionService).getRequiredFieldsByRule(rule);
        verify(conditionService).evaluateConditionsForRule(rule, facts);
        verify(actionService).executeActionsForRule(eq(rule), any());
    }

    @Test
    @DisplayName("트리거 이벤트에 따른 룰 실행 - 조건 불충족 시 액션 미실행")
    void executeTriggeredRules_withNonMatchingConditions_doesNotExecuteActions() {
        // given
        String eventType = "AI_DATA_RECEIVED";
        String eventParams = "{\"source\":\"LSTM\"}";

        Map<String, Object> facts = new HashMap<>();
        facts.put("comfort", 70);
        facts.put("location", "2");
        facts.put("sensor-name", "XYZ");

        RuleGroup ruleGroup = Mockito.mock();
        Rule rule = Rule.ofNewRule(ruleGroup, "쾌적도 알림 룰","설명", 1);
        setField(rule,"ruleNo", 1L);

        TriggerEvent trigger = TriggerEvent.ofNewTriggerEvent(rule, eventType, eventParams);
        List<TriggerEvent> triggers = List.of(trigger);

        List<String> requiredFields = List.of("comfort", "location");

        // 조건이 충족되지 않는 경우 설정
        ConditionResult conditionResult = new ConditionResult(1L, "comfort", "LT", "60", false);
        List<ConditionResult> conditionResults = List.of(conditionResult);

        when(triggerRepository.findByEventType(eventType)).thenReturn(triggers);
        when(conditionService.getRequiredFieldsByRule(rule)).thenReturn(requiredFields);
        when(conditionService.evaluateConditionsForRule(rule, facts)).thenReturn(conditionResults);

        // when
        List<RuleEvaluationResult> results = ruleEngineService.executeTriggeredRules(
                eventType, eventParams, facts);

        // then
        assertNotNull(results);
        assertEquals(1, results.size());
        RuleEvaluationResult result = results.get(0);
        assertFalse(result.isSuccess());
        assertEquals("룰 조건 불충족, 액션 미실행", result.getMessage());

        verify(triggerRepository).findByEventType(eventType);
        verify(conditionService).getRequiredFieldsByRule(rule);
        verify(conditionService).evaluateConditionsForRule(rule, facts);
        verify(actionService, never()).executeActionsForRule(any(), any());
    }

    @Test
    @DisplayName("필수 필드 누락 시 예외 발생")
    void evaluateAndExecuteRule_withMissingRequiredField_throwsException() {
        RuleGroup ruleGroup = Mockito.mock();
        Rule rule = Rule.ofNewRule(ruleGroup, "쾌적도 알림 룰","설명", 1);
        setField(rule,"ruleNo", 1L);

        Map<String, Object> facts = new HashMap<>();
        facts.put("location", "1");
        // 필수 필드 "comfort" 누락됨

        List<String> requiredFields = List.of("comfort", "location");

        when(conditionService.getRequiredFieldsByRule(rule)).thenReturn(requiredFields);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            ruleEngineService.evaluateAndExecuteRule(rule, facts);
        });

        assertTrue(exception.getMessage().contains("필수 데이터 누락: comfort"));
        verify(conditionService).getRequiredFieldsByRule(rule);
        verify(conditionService, never()).evaluateConditionsForRule(any(), any());
    }

    @Test
    @DisplayName("수동 룰 실행 - 성공")
    void executeRule_success() {
        // given
        Long ruleNo = 1L;

        Map<String, Object> facts = new HashMap<>();
        facts.put("comfort", 45);
        facts.put("location", "3");
        facts.put("sensor-name", "DEF");

        RuleGroup ruleGroup = Mockito.mock();
        Rule rule = Rule.ofNewRule(ruleGroup, "쾌적도 알림 룰","설명", 1);
        setField(rule,"ruleNo", 1L);

        List<String> requiredFields = List.of("comfort", "location");
        ConditionResult conditionResult = new ConditionResult(1L, "comfort", "LT", "60", true);
        List<ConditionResult> conditionResults = List.of(conditionResult);

        ActionResult actionResult = ActionResult.ofNewActionResult(
                1L, true, "EMAIL", "이메일 발송 성공", null);
        List<ActionResult> actionResults = List.of(actionResult);

        when(ruleService.getRuleEntity(ruleNo)).thenReturn(rule);
        when(conditionService.getRequiredFieldsByRule(rule)).thenReturn(requiredFields);
        when(conditionService.evaluateConditionsForRule(rule, facts)).thenReturn(conditionResults);
        when(actionService.executeActionsForRule(eq(rule), any())).thenReturn(actionResults);

        // when
        RuleEvaluationResult result = ruleEngineService.executeRule(ruleNo, facts);

        // then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("룰 조건 충족, 액션 실행 완료", result.getMessage());
        assertEquals(1, result.getExecutedActions().size());

        verify(ruleService).getRuleEntity(ruleNo);
        verify(conditionService).getRequiredFieldsByRule(rule);
        verify(actionService).executeActionsForRule(eq(rule), any());
    }

    @Test
    @DisplayName("수동 룰 실행 - 존재하지 않는 룰 ID")
    void executeRule_withNonExistentRuleId_throwsException() {
        // given
        Long nonExistentRuleNo = 999L;
        Map<String, Object> facts = new HashMap<>();

        when(ruleService.getRuleEntity(nonExistentRuleNo))
                .thenThrow(new RuleNotFoundException(nonExistentRuleNo));

        // when & then
        assertThrows(RuleNotFoundException.class, () -> {
            ruleEngineService.executeRule(nonExistentRuleNo, facts);
        });

        verify(ruleService).getRuleEntity(nonExistentRuleNo);
        verify(conditionService, never()).getRequiredFieldsByRule(any());
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