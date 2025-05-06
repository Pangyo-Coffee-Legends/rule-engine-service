package com.nhnacademy.ruleengineservice.registry;

import com.nhnacademy.ruleengineservice.exception.action.UnsupportedActionTypeException;
import com.nhnacademy.ruleengineservice.handler.ActionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ActionHandlerRegistryTest {

    @Mock
    ActionHandler emailHandler;

    @Mock
    ActionHandler webhookHandler;

    @Test
    @DisplayName("존재하는 액션 타입 조회 성공")
    void getHandler_success() {
        Mockito.when(emailHandler.supports("EMAIL")).thenReturn(true);

        ActionHandlerRegistry registry = new ActionHandlerRegistry(List.of(emailHandler, webhookHandler));

        ActionHandler found = registry.getHandler("EMAIL");

        assertEquals(emailHandler, found);

        verify(emailHandler).supports("EMAIL");
    }

    @Test
    @DisplayName("존재하지 않는 액션 타입 조회")
    void getHanler_Already_Exists_Exception() {
        ActionHandlerRegistry registry = new ActionHandlerRegistry(List.of(emailHandler));

        assertThrows(UnsupportedActionTypeException.class, () -> registry.getHandler("LOG"));
    }
}