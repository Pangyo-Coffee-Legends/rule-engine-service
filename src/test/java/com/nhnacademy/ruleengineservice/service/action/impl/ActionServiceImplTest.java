package com.nhnacademy.ruleengineservice.service.action.impl;

import com.nhnacademy.ruleengineservice.domain.action.Action;
import com.nhnacademy.ruleengineservice.domain.rule.Rule;
import com.nhnacademy.ruleengineservice.domain.rule.RuleGroup;
import com.nhnacademy.ruleengineservice.dto.action.ActionRegisterRequest;
import com.nhnacademy.ruleengineservice.dto.action.ActionResponse;
import com.nhnacademy.ruleengineservice.dto.action.ActionResult;
import com.nhnacademy.ruleengineservice.exception.action.ActionNotFoundException;
import com.nhnacademy.ruleengineservice.exception.rule.RuleNotFoundException;
import com.nhnacademy.ruleengineservice.handler.ActionHandler;
import com.nhnacademy.ruleengineservice.registry.ActionHandlerRegistry;
import com.nhnacademy.ruleengineservice.repository.action.ActionRepository;
import com.nhnacademy.ruleengineservice.service.rule.impl.RuleServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class ActionServiceImplTest {

    @Mock
    private RuleServiceImpl ruleService;

    @Mock
    private ActionRepository actionRepository;

    @Mock
    private ActionHandlerRegistry actionHandlerRegistry;

    @InjectMocks
    private ActionServiceImpl actionService;

    @Test
    @DisplayName("액션 등록 성공")
    void registerAction() {
        Long ruleNo = 1L;
        String actType = "EMAIL";
        String actParam = "{\"to\":\"test@example.com\",\"subject\":\"Test Subject\"}";
        Integer actPriority = 1;

        ActionRegisterRequest request = new ActionRegisterRequest(ruleNo, actType, actParam, actPriority);

        RuleGroup group = RuleGroup.ofNewRuleGroup("test1", "des1", 1);
        Rule mockRule = Rule.ofNewRule(group, "rule1", "r d1", 1);
        setField(mockRule, "ruleNo", 1L);

        Action savedAction = Action.ofNewAction(mockRule, actType, actParam, actPriority);
        setField(savedAction, "actNo", 1L);

        when(ruleService.getRuleEntity(ruleNo)).thenReturn(mockRule);
        when(actionRepository.save(Mockito.any())).thenReturn(savedAction);

        ActionResponse result = actionService.registerAction(request);

        assertNotNull(result);
        verify(ruleService).getRuleEntity(ruleNo);
        verify(actionRepository).save(Mockito.any());
    }

    @Test
    @DisplayName("존재하지 않는 규칙으로 액션 등록 시 예외 발생")
    void registerAction_withNonExistentRule_throwsException() {
        Long nonExistentRuleNo = 999L;
        ActionRegisterRequest request = new ActionRegisterRequest(
                nonExistentRuleNo, "EMAIL", "{}", 1);

        when(ruleService.getRuleEntity(nonExistentRuleNo))
                .thenThrow(new RuleNotFoundException(nonExistentRuleNo));

        assertThrows(RuleNotFoundException.class, () -> actionService.registerAction(request));
    }

    @Test
    @DisplayName("액션 삭제 성공")
    void deleteAction() {
        Long actionNo = 1L;
        when(actionRepository.existsById(actionNo)).thenReturn(true);

        actionService.deleteAction(actionNo);

        verify(actionRepository).existsById(actionNo);
        verify(actionRepository).deleteById(actionNo);
    }

    @Test
    @DisplayName("존재하지 않는 액션 삭제 시 예외 발생")
    void deleteAction_withNonExistentAction_throwsException() {
        // given
        Long nonExistentActionNo = 999L;
        when(actionRepository.existsById(nonExistentActionNo)).thenReturn(false);

        // when & then
        assertThrows(ActionNotFoundException.class, () -> actionService.deleteAction(nonExistentActionNo));
        verify(actionRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("액션 단건 조회 성공")
    void getAction() {
        Long actionNo = 1L;

        RuleGroup group = RuleGroup.ofNewRuleGroup("test1", "des1", 1);
        Rule mockRule = Rule.ofNewRule(group, "rule1", "rule d1", 1);

        Action mockAction = Action.ofNewAction(mockRule, "EMAIL", "{\"to\":\"test@example.com\"}", 1);
        setField(mockAction, "actNo", actionNo);

        when(actionRepository.findById(actionNo)).thenReturn(Optional.of(mockAction));

        ActionResponse response = actionService.getAction(actionNo);

        assertNotNull(response);
        assertAll(
                () -> assertEquals(actionNo, response.getActNo()),
                () -> assertEquals(mockRule.getRuleNo(), response.getRuleNo()),
                () -> assertEquals("EMAIL", response.getActType()),
                () -> assertEquals("{\"to\":\"test@example.com\"}", response.getActParam())
        );

        verify(actionRepository).findById(actionNo);
    }

    @Test
    @DisplayName("규칙별 액션 목록 조회 성공")
    void getActionsByRule_ValidRuleNo_ReturnsActionResponses() {
        Long ruleNo = 1L;
        RuleGroup ruleGroup = mock();
        Rule mockRule = Rule.ofNewRule(ruleGroup, "Test Rule", "Description", 1);
        Action action1 = Action.ofNewAction(mockRule, "ALERT", "Message1", 1);
        Action action2 = Action.ofNewAction(mockRule, "NOTIFICATION", "Message2", 2);
        List<Action> mockActions = List.of(action1, action2);

        when(ruleService.getRuleEntity(ruleNo)).thenReturn(mockRule);
        when(actionRepository.findByRule(mockRule)).thenReturn(mockActions);

        List<ActionResponse> result = actionService.getActionsByRule(ruleNo);

        assertEquals(2, result.size());
        assertEquals("ALERT", result.get(0).getActType());
        assertEquals("NOTIFICATION", result.get(1).getActType());

        verify(ruleService).getRuleEntity(ruleNo);
        verify(actionRepository).findByRule(mockRule);
    }

    @Test
    @DisplayName("모든 액션 조회")
    void getActions_ReturnsAllActionResponses() {
        Rule rule = mock();
        Action action1 = Action.ofNewAction(rule, "ALERT", "Message1", 1);
        Action action2 = Action.ofNewAction(rule, "NOTIFICATION", "Message2", 2);
        List<Action> mockActions = List.of(action1, action2);

        when(actionRepository.findAll()).thenReturn(mockActions);

        List<ActionResponse> result = actionService.getActions();

        assertEquals(2, result.size());
        assertEquals("Message1", result.get(0).getActParam());
        assertEquals("Message2", result.get(1).getActParam());
        verify(actionRepository).findAll();
    }

    @Test
    @DisplayName("액션이 존재하지 않음")
    void performAction_whenActionNotFound_shouldThrowException() {
        Long actionNo = 1L;
        Map<String, Object> context = Map.of();
        when(actionRepository.findById(actionNo)).thenReturn(Optional.empty());

        assertThrows(ActionNotFoundException.class, () -> actionService.performAction(actionNo, context));
    }

    @Test
    @DisplayName("performAction 성공")
    void performAction_whenHandlerSucceeds_shouldReturnSuccessResult() {
        Long actionNo = 1L;
        Action action = mock();

        when(action.getActType()).thenReturn("EMAIL");
        when(actionRepository.findById(actionNo)).thenReturn(Optional.of(action));

        ActionHandler handler = mock();
        Map<String, Object> context = Map.of();
        ActionResult expectedResult = new ActionResult(
                actionNo,
                true,
                "EMAIL",
                "이메일 전송 성공",
                null,
                LocalDateTime.now()
        );

        when(actionHandlerRegistry.getHandler("EMAIL")).thenReturn(handler);
        when(handler.handle(action, context)).thenReturn(expectedResult);

        ActionResult actualResult = actionService.performAction(actionNo, context);

        assertEquals(expectedResult.getActNo(), actualResult.getActNo());
        verify(handler).handle(action, context);
    }

    @Test
    @DisplayName("핸들러 실행 중 예외 발생시 실패 결과 반환")
    void performAction_whenHandlerThrowsException_shouldReturnFailureResult() {
        Long actionNo = 1L;
        Action action = mock();

        when(action.getActType()).thenReturn("WEBHOOK");
        when(actionRepository.findById(actionNo)).thenReturn(Optional.of(action));

        ActionHandler handler = mock(ActionHandler.class);
        when(actionHandlerRegistry.getHandler("WEBHOOK")).thenReturn(handler);
        when(handler.handle(action, Map.of())).thenThrow(new RuntimeException("외부 API 실패"));

        ActionResult result = actionService.performAction(actionNo, Map.of());

        assertFalse(result.isSuccess());
        assertEquals("액션 실행 중 오류: 외부 API 실패", result.getMessage());
    }

    @Test
    @DisplayName("액션 실행 중 예외 발생 시 실패 결과 반환")
    void performAction_exceptionThrown_returnsFailure() {
        Long actionNo = 1L;
        Map<String, Object> context = new HashMap<>();

        RuleGroup group = RuleGroup.ofNewRuleGroup("test1", "des1", 1);
        Rule mockRule = Rule.ofNewRule(group, "rule1", "rule d1", 1);

        Action mockAction = Action.ofNewAction(mockRule, "EMAIL", "invalid_json", 1);
        setField(mockAction, "actNo", actionNo);

        when(actionRepository.findById(actionNo)).thenReturn(Optional.of(mockAction));

        ActionResult result = actionService.performAction(actionNo, context);
        log.debug("예외 발생 확인 : {}", result);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("액션 실행 중 오류"));
    }

    @Test
    @DisplayName("룰에 연결된 모든 액션 실행")
    void executeActionsForRule_multipleActions() {
        RuleGroup group = RuleGroup.ofNewRuleGroup("test1", "des1", 1);
        Rule mockRule = Rule.ofNewRule(group, "rule1", "rule d1", 1);
        setField(mockRule, "ruleNo", 1L);

        Action action1 = Action.ofNewAction(mockRule, "EMAIL", "{}", 1);
        Action action2 = Action.ofNewAction(mockRule, "LOG", "{}", 2);
        setField(action1, "actNo", 1L);
        setField(action2, "actNo", 2L);
        List<Action> actions = List.of(action1, action2);

        when(actionRepository.findByRule(mockRule)).thenReturn(actions);
        when(actionRepository.findById(anyLong())).thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            return Optional.of(actions.stream().filter(a -> a.getActNo().equals(id)).findFirst().orElseThrow());
        });

        List<ActionResult> results = actionService.executeActionsForRule(mockRule, new HashMap<>());

        assertEquals(2, results.size());
        assertEquals("EMAIL", results.get(0).getActType());
        assertEquals("LOG", results.get(1).getActType());
    }


    @Test
    @DisplayName("액션 실행 - 지원하지 않는 타입")
    void performAction_unsupportedType() {
        Long actionNo = 1L;
        Action action = mock();

        when(action.getActType()).thenReturn("INVALID_TYPE");
        when(actionRepository.findById(actionNo)).thenReturn(Optional.of(action));

        when(actionHandlerRegistry.getHandler("INVALID_TYPE"))
                .thenThrow(new UnsupportedOperationException("지원하지 않는 타입"));

        ActionResult result = actionService.performAction(actionNo, Map.of());

        assertFalse(result.isSuccess());
        assertEquals("액션 실행 중 오류: 지원하지 않는 타입", result.getMessage());
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