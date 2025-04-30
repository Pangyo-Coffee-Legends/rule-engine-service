package com.nhnacademy.ruleengineservice.service.action.impl;

import com.nhnacademy.ruleengineservice.domain.action.Action;
import com.nhnacademy.ruleengineservice.domain.rule.Rule;
import com.nhnacademy.ruleengineservice.domain.rule.RuleGroup;
import com.nhnacademy.ruleengineservice.dto.action.ActionRegisterRequest;
import com.nhnacademy.ruleengineservice.dto.action.ActionResponse;
import com.nhnacademy.ruleengineservice.dto.action.ActionResult;
import com.nhnacademy.ruleengineservice.dto.comfort.ComfortInfoDTO;
import com.nhnacademy.ruleengineservice.exception.action.ActionNotFoundException;
import com.nhnacademy.ruleengineservice.exception.rule.RuleNotFoundException;
import com.nhnacademy.ruleengineservice.repository.action.ActionRepository;
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
class ActionServiceImplTest {

    @Mock
    private RuleServiceImpl ruleService;

    @Mock
    private ActionRepository actionRepository;

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
    void getActionsByRule() {
        Long ruleNo = 1L;

        RuleGroup group = RuleGroup.ofNewRuleGroup("test1", "des1", 1);
        Rule mockRule = Rule.ofNewRule(group, "rule1", "rule d1", 1);
        setField(mockRule, "ruleNo", 1L);

        List<Action> mockActions = new ArrayList<>();

        Action action1 = Action.ofNewAction(mockRule, "EMAIL", "test@example.com", 1);
        setField(action1, "actNo", 1L);

        Action action2 = Action.ofNewAction(mockRule, "SMS", "합격했습니다.", 2);
        setField(action2, "actNo", 2L);

        mockActions.add(action1);
        mockActions.add(action2);

        when(ruleService.getRuleEntity(ruleNo)).thenReturn(mockRule);
        when(actionRepository.findByRule(mockRule)).thenReturn(mockActions);

        List<ActionResponse> responses = actionService.getActionsByRule(ruleNo);

        assertNotNull(responses);
        assertAll(
                () -> assertEquals(2, responses.size()),
                () -> assertEquals("EMAIL", responses.get(0).getActType()),
                () -> assertEquals("SMS", responses.get(1).getActType())
        );
    }

    @Test
    @DisplayName("액션 실행 - EMAIL 성공 (유효한 파라미터)")
    void performAction_EMAIL_success() {
        // Given
        Long actionNo = 1L;
        Map<String, Object> context = new HashMap<>();

        // 유효한 JSON 파라미터 생성
        String validJsonParams = "{\"to\":\"test@example.com\",\"subject\":\"Test Subject\",\"body\":\"Test Body\"}";

        RuleGroup group = RuleGroup.ofNewRuleGroup("test1", "des1", 1);
        Rule mockRule = Rule.ofNewRule(group, "rule1", "rule d1", 1);

        Action mockAction = Action.ofNewAction(mockRule, "EMAIL", validJsonParams, 1);
        setField(mockAction, "actNo", actionNo);

        when(actionRepository.findById(actionNo)).thenReturn(Optional.of(mockAction));

        // When
        ActionResult result = actionService.performAction(actionNo, context);

        // Then
        assertAll(
                () -> assertTrue(result.isSuccess()),
                () -> assertEquals("EMAIL", result.getActType()),
                () -> assertEquals("이메일 발송 성공", result.getMessage()),
                () -> assertNull(result.getOutput()) // 이메일은 output 없음
        );

        log.info("이메일 발송 결과: {}", result);
    }


    @Test
    @DisplayName("액션 실행 - WEBHOOK 성공")
    void performAction_WEBHOOK_success() {
        Long actionNo = 1L;
        Map<String, Object> context = new HashMap<>();
        context.put("url", "http://example.com/webhook");

        RuleGroup group = RuleGroup.ofNewRuleGroup("test1", "des1", 1);
        Rule mockRule = Rule.ofNewRule(group, "rule1", "rule d1", 1);
        Action mockAction = Action.ofNewAction(mockRule, "WEBHOOK", "{}", 1);
        setField(mockAction, "actNo", actionNo);

        when(actionRepository.findById(actionNo)).thenReturn(Optional.of(mockAction));

        ActionResult result = actionService.performAction(actionNo, context);

        assertTrue(result.isSuccess());
        assertAll(
                () -> assertEquals("WEBHOOK", result.getActType()),
                () -> assertEquals("웹훅 호출 성공", result.getMessage())
        );
    }

    @Test
    @DisplayName("액션 실행 - LOG 성공")
    void performAction_LOG_success() {
        Long actionNo = 1L;
        Map<String, Object> context = new HashMap<>();
        context.put("message", "Test log message");

        RuleGroup group = RuleGroup.ofNewRuleGroup("test1", "des1", 1);
        Rule mockRule = Rule.ofNewRule(group, "rule1", "rule d1", 1);
        Action mockAction = Action.ofNewAction(mockRule, "LOG", "{}", 1);
        setField(mockAction, "actNo", actionNo);

        when(actionRepository.findById(actionNo)).thenReturn(Optional.of(mockAction));

        ActionResult result = actionService.performAction(actionNo, context);

        assertTrue(result.isSuccess());
        assertAll(
                () -> assertEquals("LOG", result.getActType()),
                () -> assertEquals("로그 기록 성공", result.getMessage())
        );
    }

    @Test
    @DisplayName("액션 실행 - COMFORT_NOTIFICATION 성공")
    void performAction_COMFORT_NOTIFICATION_success() {
        Long actionNo = 1L;
        Map<String, Object> context = new HashMap<>();
        context.put("location", "Room 101");
        context.put("comfortIndex", 75.5);
        context.put("comfortGrade", "Good");

        RuleGroup group = RuleGroup.ofNewRuleGroup("test1", "des1", 1);
        Rule mockRule = Rule.ofNewRule(group, "rule1", "rule d1", 1);
        Action mockAction = Action.ofNewAction(mockRule, "COMFORT_NOTIFICATION", "{}", 1);
        setField(mockAction, "actNo", actionNo);

        when(actionRepository.findById(actionNo)).thenReturn(Optional.of(mockAction));

        ActionResult result = actionService.performAction(actionNo, context);

        assertTrue(result.isSuccess());
        assertEquals("COMFORT_NOTIFICATION", result.getActType());
        assertEquals("쾌적도 알림 전송 성공", result.getMessage());
        assertNotNull(result.getOutput());
        assertInstanceOf(ComfortInfoDTO.class, result.getOutput());
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
        Map<String, Object> context = new HashMap<>();

        RuleGroup group = RuleGroup.ofNewRuleGroup("test1", "des1", 1);
        Rule mockRule = Rule.ofNewRule(group, "rule1", "rule d1", 1);

        Action mockAction = Action.ofNewAction(mockRule, "UNKNOWN_TYPE", "{}", 1);
        setField(mockAction, "actNo", actionNo);

        when(actionRepository.findById(actionNo)).thenReturn(Optional.of(mockAction));

        ActionResult result = actionService.performAction(actionNo, context);

        assertFalse(result.isSuccess());
        assertEquals("UNKNOWN_TYPE", result.getActType());
        assertEquals("지원하지 않는 액션 타입", result.getMessage());
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