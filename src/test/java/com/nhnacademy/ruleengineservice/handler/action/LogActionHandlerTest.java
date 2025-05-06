package com.nhnacademy.ruleengineservice.handler.action;

import com.nhnacademy.ruleengineservice.domain.action.Action;
import com.nhnacademy.ruleengineservice.dto.action.ActionResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class LogActionHandlerTest {

    private final LogActionHandler handler = new LogActionHandler();

    @Test
    @DisplayName("LOG 인지 체크")
    void supports() {
        assertTrue(handler.supports("LOG"));
        assertFalse(handler.supports("EMAIL"));
    }

    @Test
    @DisplayName("Log 보내기 성공")
    void handle() {
        Action action = mock();

        ActionResult result = handler.handle(action, new HashMap<>());
        assertTrue(result.isSuccess());
        assertEquals("로그 기록 성공", result.getMessage());
    }
}