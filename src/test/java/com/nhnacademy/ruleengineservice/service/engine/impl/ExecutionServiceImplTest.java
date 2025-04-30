package com.nhnacademy.ruleengineservice.service.engine.impl;

import com.nhnacademy.ruleengineservice.domain.action.Action;
import com.nhnacademy.ruleengineservice.domain.rule.Rule;
import com.nhnacademy.ruleengineservice.domain.rule.RuleGroup;
import com.nhnacademy.ruleengineservice.dto.action.ActionResult;
import com.nhnacademy.ruleengineservice.service.action.ActionService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
@DataJpaTest
@ActiveProfiles("test")
class ExecutionServiceImplTest {

    @Mock
    private ActionService actionService;

    @InjectMocks
    private ExecutionServiceImpl executionService;

    @Test
    @DisplayName("단일 액션 실행 테스트")
    void executeActions() {
        RuleGroup ruleGroup = RuleGroup.ofNewRuleGroup("그룹","그룹설명",1);
        Rule rule = Rule.ofNewRule(ruleGroup, "롤","롤설명",1);

        Action action = Action.ofNewAction(rule, "EMAIL", "test@example.com", 1);

        Map<String, Object> context = new HashMap<>();
        context.put("recipient", "test@example.com");

        ActionResult actionResult = ActionResult.ofNewActionResult(1L, true, "EMAIL", "이메일 발송 성공", null);

        when(actionService.performAction(action.getActNo(), context)).thenReturn(actionResult);

        ActionResult result = executionService.executeAction(action, context);

        assertNotNull(result);
        assertAll(
                () -> assertEquals(1L, result.getActNo()),
                () -> assertEquals("EMAIL", result.getActType()),
                () -> assertEquals("이메일 발송 성공", result.getMessage()),
                () -> assertTrue(result.isSuccess())
        );

        Mockito.verify(actionService).performAction(action.getActNo(), context);
    }

    @Test
    @DisplayName("룰에 연결된 여러 액션 실행 테스트")
    void executeActions_withMultipleActions_success() {
        Rule rule = mock(Rule.class);

        Action action1 = Action.ofNewAction(rule, "EMAIL", "test@example.com", 1);
        Action action2 = Action.ofNewAction(rule, "SMS", "합격을 축하합니다.", 2);

        setField(action1, 1L);
        setField(action2, 2L);

        List<Action> actions = new ArrayList<>();
        actions.add(action1);
        actions.add(action2);

        // rule.getActionList()가 사용되므로 모의 설정
        Mockito.when(rule.getActionList()).thenReturn(actions);

        Map<String, Object> facts = new HashMap<>();
        facts.put("temperature", 30);

        ActionResult result1 = ActionResult.ofNewActionResult(
                1L, true, "EMAIL", "이메일 발송 성공", null);
        ActionResult result2 = ActionResult.ofNewActionResult(
                2L, true, "SMS", "SMS 발송 성공", null);

        when(actionService.performAction(1L, facts)).thenReturn(result1);
        when(actionService.performAction(2L, facts)).thenReturn(result2);

        List<ActionResult> results = executionService.executeActions(rule, facts);

        assertNotNull(results);
        assertEquals(2, results.size());

        assertEquals(1L, results.get(0).getActNo());
        assertEquals("EMAIL", results.get(0).getActType());

        assertEquals(2L, results.get(1).getActNo());
        assertEquals("SMS", results.get(1).getActType());

        Mockito.verify(actionService, Mockito.times(1)).performAction(1L, facts);
        Mockito.verify(actionService, Mockito.times(1)).performAction(2L, facts);
    }

    @Test
    @DisplayName("액션 실패 시 테스트")
    void executeAction_whenActionFails() {
        Rule rule = Mockito.mock(Rule.class);

        Action action = Action.ofNewAction(rule, "EMAIL", "asdf@aasdf.acd",1);
        setField(action, 1L);

        Map<String, Object> context = new HashMap<>();

        ActionResult failedResult = ActionResult.ofNewActionResult(
                1L, false, "EMAIL", "이메일 발송 실패: 수신자 없음", null);

        when(actionService.performAction(action.getActNo(), context)).thenReturn(failedResult);

        ActionResult result = executionService.executeAction(action, context);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals("이메일 발송 실패: 수신자 없음", result.getMessage());

        Mockito.verify(actionService).performAction(action.getActNo(), context);
    }

    @Test
    @DisplayName("빈 액션 리스트 테스트")
    void executeActions_withEmptyActionList() {
        Rule rule = Mockito.mock(Rule.class);
        when(rule.getActionList()).thenReturn(new ArrayList<>());

        Map<String, Object> facts = new HashMap<>();

        List<ActionResult> results = executionService.executeActions(rule, facts);

        assertNotNull(results);
        assertTrue(results.isEmpty());

        Mockito.verify(actionService, Mockito.never()).performAction(Mockito.anyLong(), Mockito.any());
    }

    private void setField(Object target, Object value) {
        try {
            Field field = target.getClass().getDeclaredField("actNo");
            field.setAccessible(true);
            field.set(target, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}