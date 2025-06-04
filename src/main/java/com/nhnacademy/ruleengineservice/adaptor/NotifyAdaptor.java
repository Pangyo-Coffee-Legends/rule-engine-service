package com.nhnacademy.ruleengineservice.adaptor;

import com.nhnacademy.ruleengineservice.dto.email.EmailRequest;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * {@code NotifyAdaptor}는 이메일 알림 전송을 위해 RabbitMQ 큐에 메시지를 발행하는 서비스 클래스입니다.
 * <p>
 * Spring의 {@link RabbitTemplate}을 사용해 {@code EmailRequest} 객체를 지정된 이메일 큐로 전송합니다.
 * 이메일 큐 이름은 Spring 환경설정({@code email.queue})에서 주입받습니다.
 * <ul>
 *   <li>{@link #sendEmail(EmailRequest)}: 이메일 발송 요청을 큐에 전송</li>
 * </ul>
 * Spring Boot, Spring Cloud 기반의 마이크로서비스 환경에서 비동기 메시징, API 통합, IoT 서비스 등 다양한 시나리오에 활용할 수 있습니다[1][2][3].
 * </p>
 *
 * @author 강승우
 * @since 1.0
 */
@Service
public class NotifyAdaptor {
    private final RabbitTemplate rabbitTemplate;
    private final String emailQueue;

    public NotifyAdaptor(RabbitTemplate rabbitTemplate, @Value("${email.queue}") String emailQueue) {
        this.rabbitTemplate = rabbitTemplate;
        this.emailQueue = emailQueue;
    }

    /**
     * 이메일 발송 요청을 RabbitMQ 이메일 큐에 전송합니다.
     *
     * @param request 이메일 발송 요청 데이터
     */
    public void sendEmail(EmailRequest request) {
        rabbitTemplate.convertAndSend(emailQueue, request);
    }
}
