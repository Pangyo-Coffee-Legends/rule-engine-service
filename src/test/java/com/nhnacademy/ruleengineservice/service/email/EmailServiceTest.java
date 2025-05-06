package com.nhnacademy.ruleengineservice.service.email;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("test 로 이메일 보내기")
    void sendTextEmail() {
        emailService.sendTextEmail("dusen0528@naver.com", "Test Subject", "Test Body");

        verify(mailSender, times(1)).send(Mockito.any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("html 로 이메일 보내기")
    void sendHtmlEmail() throws MessagingException {
        Session session = Session.getDefaultInstance(System.getProperties());
        MimeMessage mimeMessage = new MimeMessage(session);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendHtmlEmail("test@example.com", "Test Subject", "<h1>Test</h1>");

        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(mimeMessage);
    }
}