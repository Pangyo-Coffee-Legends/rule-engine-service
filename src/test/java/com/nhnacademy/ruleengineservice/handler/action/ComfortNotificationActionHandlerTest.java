package com.nhnacademy.ruleengineservice.handler.action;

import com.nhnacademy.ruleengineservice.domain.action.Action;
import com.nhnacademy.ruleengineservice.dto.action.ActionResult;
import com.nhnacademy.ruleengineservice.dto.comfort.ComfortInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@Slf4j
@ExtendWith(MockitoExtension.class)
class ComfortNotificationActionHandlerTest {

    @InjectMocks
    ComfortNotificationActionHandler handler;

    @Test
    void supports() {
        assertTrue(handler.supports("COMFORT_NOTIFICATION"));
        assertFalse(handler.supports("EMAIL"));
    }

    @Test
    void handle() {
        Action action = mock();

        Map<String, Object> context = new HashMap<>();
        context.put("location", "서울");
        context.put("comfortIndex", 75.5);
        context.put("comfortGrade", "좋음");

        ActionResult result = handler.handle(action, context);
        log.debug("result : {}", result);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("쾌적도 알림 전송 성공", result.getMessage());

        ComfortInfoDTO dto = (ComfortInfoDTO) result.getOutput();
        log.debug("dto : {}", dto);

        assertNotNull(dto);
        assertAll(
                () -> assertEquals("서울", dto.getLocation()),
                () -> assertEquals(75.5, dto.getComfortIndex()),
                () -> assertEquals("좋음", dto.getComfortGrade())
        );
    }
}