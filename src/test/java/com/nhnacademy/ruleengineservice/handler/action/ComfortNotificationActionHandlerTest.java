package com.nhnacademy.ruleengineservice.handler.action;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.ruleengineservice.domain.action.Action;
import com.nhnacademy.ruleengineservice.dto.action.ActionResult;
import com.nhnacademy.ruleengineservice.dto.comfort.AiCommentDTO;
import com.nhnacademy.ruleengineservice.dto.comfort.ComfortIndexDTO;
import com.nhnacademy.ruleengineservice.dto.comfort.ComfortInfoDTO;
import com.nhnacademy.ruleengineservice.dto.comfort.ComfortNotificationDTO;
import com.nhnacademy.ruleengineservice.exception.action.ActionHandlerException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class ComfortNotificationActionHandlerTest {

    @InjectMocks
    ComfortNotificationActionHandler handler;

    @Mock
    ObjectMapper objectMapper;

    @Test
    void supports() {
        assertTrue(handler.supports("COMFORT_NOTIFICATION"));
        assertFalse(handler.supports("EMAIL"));
    }

    @Test
    @DisplayName("핸들 성공")
    void handle_success() throws Exception {
        Action action = mock();
        when(action.getActNo()).thenReturn(1L);
        when(action.getActType()).thenReturn("COMFORT_NOTIFICATION");

        Map<String, Object> context = new HashMap<>();

        String validJson = """
                {
                    "location": "hybrid",
                    "comfort-index": {
                        "temperature": 30.5,
                        "humidity": 60.0,
                        "co2": 1000
                    },
                    "ai-comment": {
                        "temperature": "높음",
                        "humidity": "적정",
                        "co2": "위험"
                    }
                }
                """;
        context.put("json", validJson);

        ComfortIndexDTO indexDto = new ComfortIndexDTO(
                30.5,
                60.0,
                1000.0
        );

        AiCommentDTO commentDto = new AiCommentDTO(
                "높음",
                "적정",
                "위험"
        );

        ComfortNotificationDTO mockDto = new ComfortNotificationDTO(
                "hybrid",
                indexDto,
                commentDto
        );

        when(objectMapper.readValue(validJson, ComfortNotificationDTO.class)).thenReturn(mockDto);

        ActionResult result = handler.handle(action, context);
        log.debug("result : {}", result);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("쾌적도 알림 전송 성공", result.getMessage());

        ComfortInfoDTO dto = (ComfortInfoDTO) result.getOutput();
        log.debug("dto : {}", dto);

        assertNotNull(dto);
        assertAll(
                () -> assertEquals("hybrid", dto.getLocation()),
                () -> assertEquals(30.5, dto.getTemperature()),
                () -> assertEquals(60.0, dto.getHumidity()),
                () -> assertEquals(1000.0, dto.getCo2()),
                () -> assertEquals("높음", dto.getTemperatureComment()),
                () -> assertEquals("적정", dto.getHumidityComment()),
                () -> assertEquals("위험", dto.getCo2Comment())
        );

        verify(objectMapper).readValue(validJson, ComfortNotificationDTO.class);
    }

    @Test
    @DisplayName("핸들 제이슨 파싱 실패")
    void handle_jsonParsingFailure_shouldThrowActionHandlerException() throws Exception {
        Action action = mock();
        Map<String, Object> context = new HashMap<>();

        String invalidJson = "invalid_json";
        context.put("json", invalidJson);

        when(objectMapper.readValue(invalidJson, ComfortNotificationDTO.class))
                .thenThrow(new JsonProcessingException("파싱 실패") {});

        assertThrows(ActionHandlerException.class, () -> handler.handle(action, context));
    }

    @Test
    @DisplayName("json 키 누락")
    void handle_missingJsonKey_shouldThrowException() {
        Action action = mock();
        Map<String, Object> context = new HashMap<>();

        context.put("wrong_key", "some_value");  // "json" 키 누락

        assertThrows(ActionHandlerException.class, () -> handler.handle(action, context));
    }
}