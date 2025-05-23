package com.nhnacademy.ruleengineservice.adaptor;

import com.nhnacademy.ruleengineservice.dto.email.EmailRequest;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class NotifyAdaptor {
    private final RabbitTemplate rabbitTemplate;
    private final String emailQueue;

    public NotifyAdaptor(RabbitTemplate rabbitTemplate, @Value("${email.queue}") String emailQueue) {
        this.rabbitTemplate = rabbitTemplate;
        this.emailQueue = emailQueue;
    }

    public void sendEmail(EmailRequest request) {
        rabbitTemplate.convertAndSend(emailQueue, request);
    }
}
