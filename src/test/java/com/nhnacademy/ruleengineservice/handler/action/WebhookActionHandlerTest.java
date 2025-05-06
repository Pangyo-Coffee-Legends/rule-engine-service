package com.nhnacademy.ruleengineservice.handler.action;

import com.nhnacademy.ruleengineservice.domain.action.Action;
import com.nhnacademy.ruleengineservice.dto.action.ActionResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class WebhookActionHandlerTest {

    private final WebhookActionHandler handler = new WebhookActionHandler();

    @Test
    @DisplayName("webhook 인지 확인")
    void supports() {
        assertTrue(handler.supports("WEBHOOK"));
        assertFalse(handler.supports("EMAIL"));
    }

    @Test
    @DisplayName("웹훅 호출 성공")
    void handle() {
        Action action = mock();

        ActionResult result = handler.handle(action, new HashMap<>());
        assertTrue(result.isSuccess());
        assertEquals("웹훅 호출 성공", result.getMessage());
    }
}