package com.nhnacademy.ruleengineservice.handler.action;

import com.nhnacademy.ruleengineservice.domain.action.Action;
import com.nhnacademy.ruleengineservice.domain.rule.Rule;
import com.nhnacademy.ruleengineservice.dto.action.ActionResponse;
import com.nhnacademy.ruleengineservice.dto.action.ActionResult;
import com.nhnacademy.ruleengineservice.service.action.ActionService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ComfortNotificationActionHandlerTest {

    @InjectMocks
    ComfortNotificationActionHandler handler;

    @Mock
    ActionService actionService;

    @Test
    void supports() {
        assertTrue(handler.supports("COMFORT_NOTIFICATION"));
        assertFalse(handler.supports("EMAIL"));
    }

    @Test
    @DisplayName("덥고 습함 케이스")
    void handle_withComportIndexHotHumid() {
        Rule rule = mock();

        Action action = Action.ofNewAction(rule, "COMFORT_NOTIFICATION", "알림2", 2);
        setField(action, 1L);

        Map<String, Object> context = new HashMap<>();
        context.put("comport_index", "덥고 습함");

        ActionResponse mockResponse = new ActionResponse(1L, 10L,
                "COMFORT_NOTIFICATION", "cooling_mode", 1);
        when(actionService.getAction(1L)).thenReturn(mockResponse);

        ActionResult result = handler.handle(action, context);
        System.out.println(result);

        assertEquals(1L, result.getActNo());
        assertTrue(result.isSuccess());
        assertEquals("COMFORT_NOTIFICATION", result.getActType());
        assertEquals("쾌적도 알림 전송 성공", result.getMessage());

        Map<String, Object> output = (Map<String, Object>) result.getOutput();
        assertEquals(true, output.get("aircon"));
        assertEquals(true, output.get("dehumidifier"));
        assertEquals("cooling_mode", output.get("action"));
    }

    @Test
    @DisplayName("춥고 건조 케이스")
    void handle_withComportIndexColdDry() {
        Rule rule = mock();
        Action action = Action.ofNewAction(rule, "COMFORT_NOTIFICATION", "heater", 1);
        setField(action, 2L);

        Map<String, Object> context = new HashMap<>();
        context.put("comport_index", "춥고 건조");

        when(actionService.getAction(2L)).thenReturn(null);

        ActionResult result = handler.handle(action, context);

        Map<String, Object> output = (Map<String, Object>) result.getOutput();
        assertEquals(true, output.get("heater"));
        assertEquals(true, output.get("humidifier"));
        assertFalse(output.containsKey("action"));
    }

    @Test
    @DisplayName("최적 쾌적 케이스")
    void handle_withComportIndexOptimal() {
        Rule rule = mock();
        Action action = Action.ofNewAction(rule, "COMFORT_NOTIFICATION", "eco_mode", 1);
        setField(action, 3L);

        Map<String, Object> context = new HashMap<>();
        context.put("comport_index", "최적 쾌적");

        ActionResponse mockResponse = new ActionResponse(3L, 2L
                , "COMFORT_NOTIFICATION", "eco_mode", 1);
        when(actionService.getAction(3L)).thenReturn(mockResponse);

        ActionResult result = handler.handle(action, context);
        System.out.println(result);

        Map<String, Object> output = (Map<String, Object>) result.getOutput();
        assertEquals(false, output.get("heater"));
        assertNull(output.get("humidifier"));
        assertEquals("eco_mode", output.get("action"));
    }

    @Test
    @DisplayName("알수없는 쾌적 지수")
    void handle_withUnknownComportIndex() {
        Rule rule = mock();
        Action action = Action.ofNewAction(rule, "COMFORT_NOTIFICATION", "eco_mode", 1);
        setField(action, 4L);

        Map<String, Object> context = new HashMap<>();
        context.put("comport_index", "알 수 없음");

        ActionResult result = handler.handle(action, context);
        System.out.println(result);

        Map<String, Object> output = (Map<String, Object>) result.getOutput();
        assertFalse(output.containsKey("aircon"));
        assertFalse(output.containsKey("heater"));
        assertFalse(output.containsKey("humidifier"));
        assertFalse(output.containsKey("dehumidifier"));
    }

    @Test
    @DisplayName("컨텍스트에 쾌적지수 없음")
    void handle_withoutComportIndex() {
        Rule rule = mock();
        Action action = Action.ofNewAction(rule, "COMFORT_NOTIFICATION", "eco_mode", 1);
        setField(action, 5L);

        Map<String, Object> context = new HashMap<>();

        ActionResult result = handler.handle(action, context);

        assertEquals(Map.of(), result.getOutput());
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