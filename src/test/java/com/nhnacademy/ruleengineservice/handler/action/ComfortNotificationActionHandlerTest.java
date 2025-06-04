package com.nhnacademy.ruleengineservice.handler.action;

import com.nhnacademy.ruleengineservice.domain.action.Action;
import com.nhnacademy.ruleengineservice.domain.rule.Rule;
import com.nhnacademy.ruleengineservice.dto.action.ActionResult;
import com.nhnacademy.ruleengineservice.dto.comfort.ComfortInfoDTO;
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
    private Action mockAction;

    Rule rule;

    @Test
    @DisplayName("COMFORT_NOTIFICATION 일 때 true, 아니면 false")
    void supports() {
        assertTrue(handler.supports("COMFORT_NOTIFICATION"));
        assertFalse(handler.supports("EMAIL"));
    }

    @Test
    @DisplayName("handle: 정상 ComfortInfoDTO 입력 시 성공 결과와 명령 생성")
    void handle_WithValidComfortInfo_ReturnsSuccessResult() {
        when(mockAction.getActNo()).thenReturn(1L);
        when(mockAction.getActType()).thenReturn("COMFORT_NOTIFICATION");

        ComfortInfoDTO comfortInfo = new ComfortInfoDTO(
                "회의실",
                30.0,
                40.0,
                400.0,
                "덥고 습함",
                "CO2 주의"
        );
        Map<String, Object> context = new HashMap<>();
        context.put("comfortInfo", comfortInfo);

        ActionResult result = handler.handle(mockAction, context);

        // Then: 결과 검증
        assertTrue(result.isSuccess());
        assertEquals("쾌적도 알림 전송 성공", result.getMessage());

        // 1. output을 Map<String, Object>로 캐스팅
        Map<String, Object> output = (Map<String, Object>) result.getOutput();

        // 2. deviceCommands 추출 및 검증
        Map<String, Boolean> deviceCommands = (Map<String, Boolean>) output.get("deviceCommands");
        assertNotNull(deviceCommands);
        assertTrue(deviceCommands.get("aircon"));
        assertTrue(deviceCommands.get("dehumidifier"));
        assertTrue(deviceCommands.get("ventilator"));

        // 3. comfortInfo 추출 및 검증
        ComfortInfoDTO outputComfortInfo = (ComfortInfoDTO) output.get("comfortInfo");
        assertEquals("회의실", outputComfortInfo.getLocation());
        assertEquals(30.0, outputComfortInfo.getTemperature());
    }

    @Test
    @DisplayName("handle: ComfortInfoDTO 누락 시 실패 결과 반환")
    void handle_MissingComfortInfo_ReturnsErrorResult() {
        when(mockAction.getActNo()).thenReturn(1L);
        when(mockAction.getActType()).thenReturn("COMFORT_NOTIFICATION");

        Map<String, Object> context = new HashMap<>();

        ActionResult result = handler.handle(mockAction, context);

        assertFalse(result.isSuccess());
        assertEquals("ComfortInfoDTO 누락", result.getMessage());
    }

    @Test
    @DisplayName("handle: comfortIndex가 null 이면 ventilator 만 true, 나머지 명령 없음")
    void handle_NullComfortIndex_GeneratesPartialCommands() {
        when(mockAction.getActNo()).thenReturn(1L);
        when(mockAction.getActType()).thenReturn("COMFORT_NOTIFICATION");

        ComfortInfoDTO comfortInfo = new ComfortInfoDTO(
                "회의실",       // location
                30.0,          // temperature
                40.0,          // humidity
                400.0,         // co2
                null,          // comfortIndex (null로 설정)
                "CO2 주의"     // co2Comment
        );
        Map<String, Object> context = new HashMap<>();
        context.put("comfortInfo", comfortInfo);

        ActionResult result = handler.handle(mockAction, context);

        assertTrue(result.isSuccess());

        Map<String, Object> output = (Map<String, Object>) result.getOutput();
        Map<String, Boolean> commands = (Map<String, Boolean>) output.get("deviceCommands");

        // CO2 주의로 ventilator는 true
        assertTrue(commands.get("ventilator"));

        // comfortIndex가 null이므로 aircon, dehumidifier 명령 없음
        assertFalse(commands.containsKey("aircon"));
        assertFalse(commands.containsKey("dehumidifier"));
    }

    @Test
    @DisplayName("handle: 지원되지 않는 comfortIndex면 ventilator만 true, 나머지 명령 없음")
    void handle_UnsupportedComfortIndex_GeneratesNoDeviceCommandsExceptVentilator() {
        when(mockAction.getActNo()).thenReturn(1L);
        when(mockAction.getActType()).thenReturn("COMFORT_NOTIFICATION");

        ComfortInfoDTO comfortInfo = new ComfortInfoDTO(
                "회의실",
                30.0,
                40.0,
                400.0,
                "알 수 없는 상태", // comfortIndex (지원되지 않는 값)
                "CO2 주의"
        );
        Map<String, Object> context = new HashMap<>();
        context.put("comfortInfo", comfortInfo);

        ActionResult result = handler.handle(mockAction, context);

        assertTrue(result.isSuccess());

        Map<String, Object> output = (Map<String, Object>) result.getOutput();
        Map<String, Boolean> commands = (Map<String, Boolean>) output.get("deviceCommands");

        assertFalse(commands.containsKey("aircon"));
        assertFalse(commands.containsKey("dehumidifier"));
        assertFalse(commands.containsKey("heater"));
        assertFalse(commands.containsKey("humidifier"));

        // co2Comment가 "CO2 주의"이므로 ventilator 는 true
        assertTrue(commands.get("ventilator"));
    }

    @Test
    @DisplayName("컨텍스트에 쾌적지수 없음")
    void handle_withoutComfortIndex() {
        rule = mock();
        Action action = Action.ofNewAction(rule, "COMFORT_NOTIFICATION", "eco_mode", 1);
        setField(action, 5L);

        Map<String, Object> context = new HashMap<>();

        ActionResult result = handler.handle(action, context);

        assertNull(result.getOutput());
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