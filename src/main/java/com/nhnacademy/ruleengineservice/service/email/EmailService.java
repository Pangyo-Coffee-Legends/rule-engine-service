package com.nhnacademy.ruleengineservice.service.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * 이메일 발송 기능을 제공하는 서비스 클래스입니다.
 * <p>
 * 텍스트 및 HTML 형식의 이메일을 발송할 수 있으며,
 * 내부적으로 Spring의 {@link JavaMailSender}를 사용합니다.
 * </p>
 *
 * @author 강승우
 */
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    /**
     * EmailService의 생성자입니다.
     *
     * @param mailSender 이메일 발송에 사용할 JavaMailSender 빈
     */
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * 일반 텍스트 형식의 이메일을 발송합니다.
     *
     * @param to      수신자 이메일 주소
     * @param subject 이메일 제목
     * @param text    이메일 본문(텍스트)
     */
    public void sendTextEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    /**
     * HTML 형식의 이메일을 발송합니다.
     *
     * @param to          수신자 이메일 주소
     * @param subject     이메일 제목
     * @param htmlContent 이메일 본문(HTML)
     * @throws MessagingException 이메일 생성 또는 발송 중 오류가 발생한 경우
     */
    public void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        mailSender.send(message);
    }
}
