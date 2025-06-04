package com.nhnacademy.ruleengineservice.handler.action;

import com.nhnacademy.ruleengineservice.adaptor.NotifyAdaptor;
import com.nhnacademy.ruleengineservice.domain.action.Action;
import com.nhnacademy.ruleengineservice.dto.action.ActionResult;
import com.nhnacademy.ruleengineservice.exception.action.ActionHandlerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class EmailActionHandlerTest {

    @Mock
    private NotifyAdaptor notifyAdaptor;


    @InjectMocks
    EmailActionHandler handler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new EmailActionHandler(notifyAdaptor);
    }

    @Test
    @DisplayName("supports 메서드는 actType이 EMAIL일 때 true, 아니면 false를 반환한다")
    void testSupports() {
        assertTrue(handler.supports("EMAIL"));
        assertFalse(handler.supports("OTHER"));
    }

    @Test
    @DisplayName("정상적인 EmailRequest를 처리하면 성공 결과를 반환한다")
    void testHandleSuccess() {
        String json = "{\"to\":\"test@example.com\",\"subject\":\"Hello\",\"body\":\"World\",\"type\":\"TEXT\"}";
        Action action = mock(Action.class);
        when(action.getActParams()).thenReturn(json);
        when(action.getActNo()).thenReturn(1L);
        when(action.getActType()).thenReturn("EMAIL");

        ActionResult result = handler.handle(action, new HashMap<>());

        verify(notifyAdaptor, times(1)).sendEmail(any());
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("이메일 발송 성공", result.getMessage());
    }

    @Test
    @DisplayName("JSON 파싱 실패 시 ActionHandlerException이 발생한다")
    void testHandleJsonParsingException() {
        String invalidJson = "invalid json";
        Action action = mock(Action.class);
        when(action.getActParams()).thenReturn(invalidJson);

        assertThrowsExactly(ActionHandlerException.class, () -> handler.handle(action, new HashMap<>()));

        verify(notifyAdaptor, never()).sendEmail(any());
    }

    @Test
    @DisplayName("필수 필드 누락 시 ActionHandlerException이 발생한다")
    void testHandleMissingField() {
        String jsonMissingBody = "{\"to\":\"test@example.com\",\"subject\":\"Hello\"}";
        Action action = mock(Action.class);
        when(action.getActParams()).thenReturn(jsonMissingBody);

        assertThrowsExactly(ActionHandlerException.class, () -> handler.handle(action, new HashMap<>()));

        verify(notifyAdaptor, never()).sendEmail(any());
    }

    @Test
    @DisplayName("notifyAdaptor.sendEmail에서 예외 발생 시 ActionHandlerException이 발생한다")
    void testHandleEmailRequestException() {
        String json = "{\"to\":\"test@example.com\",\"subject\":\"Hello\",\"body\":\"World\"}";
        Action action = mock(Action.class);
        when(action.getActParams()).thenReturn(json);

        doThrow(new RuntimeException("Send failed")).when(notifyAdaptor).sendEmail(any());

        assertThrowsExactly(ActionHandlerException.class, () -> handler.handle(action, new HashMap<>()));
    }
}