package com.nhnacademy.ruleengineservice.handler.action;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.ruleengineservice.domain.action.Action;
import com.nhnacademy.ruleengineservice.dto.action.ActionResult;
import com.nhnacademy.ruleengineservice.exception.action.ActionHandlerException;
import com.nhnacademy.ruleengineservice.service.email.EmailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailActionHandlerTest {

    @Mock
    private EmailService emailService;

    @Mock
    ObjectMapper objectMapper;

    @InjectMocks
    EmailActionHandler handler;

    @Test
    @DisplayName("현재가 EMAIL 인지 확인")
    void supports() {
        assertTrue(handler.supports("EMAIL"));
        assertFalse(handler.supports("WEBHOOK"));
    }

    @Test
    @DisplayName("이메일 보내기 성공")
    void handle_success() {
        Action action = mock();
        when(action.getActNo()).thenReturn(1L);
        when(action.getActType()).thenReturn("EMAIL");
        String params = "{\"to\":\"test@domain.com\",\"subject\":\"제목\",\"body\":\"본문\"}";
        when(action.getActParams()).thenReturn(params);

        ActionResult result = handler.handle(action, new HashMap<>());

        // then
        verify(emailService).sendTextEmail("test@domain.com", "제목", "본문");
        assertTrue(result.isSuccess());
        assertEquals("이메일 발송 성공", result.getMessage());
    }

    @Test
    @DisplayName("이메일 보내기 실패")
    void handle_whenEmailSendingFails_shouldThrowException() {
        Action action = mock();
        String params = "{\"to\":\"test@domain.com\",\"subject\":\"제목\",\"body\":\"본문\"}";
        when(action.getActParams()).thenReturn(params);

        // EmailService.sendTextEmail() 호출 시 예외 발생하도록 설정
        doThrow(new MailSendException("SMTP 서버 연결 실패"))
                .when(emailService)
                .sendTextEmail(anyString(), anyString(), anyString());

        Throwable thrown = catchThrowable(() -> handler.handle(action, new HashMap<>()));

        assertThat(thrown)
                .isInstanceOf(MailSendException.class)
                .hasMessageContaining("SMTP 서버 연결 실패");

        verify(emailService).sendTextEmail("test@domain.com", "제목", "본문");
    }

    @Test
    @DisplayName("Json 형식이 아닌 경우")
    void handle_whenJsonProcessingException_thenThrowActionHandlerException() throws Exception {
        Action action = mock(Action.class);
        String params = "잘못된 JSON";

        when(action.getActParams()).thenReturn(params);

        lenient().when(objectMapper.readTree(params))
                .thenThrow(new JsonProcessingException("파싱 실패") {});

        Map<String, Object> context = Map.of();
        assertThrows(ActionHandlerException.class, () -> handler.handle(action, context));
    }
}