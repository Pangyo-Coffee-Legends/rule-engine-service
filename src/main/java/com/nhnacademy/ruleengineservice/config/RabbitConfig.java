package com.nhnacademy.ruleengineservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 관련 설정을 담당하는 Configuration 클래스입니다.
 */
@Configuration
public class RabbitConfig {

    /**
     * 이메일 발송용 큐 이름을 외부 프로퍼티에서 주입받습니다.
     */
    @Value("${email.queue}")
    private String emailQueue;

    /**
     * 이메일 발송용 원본 큐를 생성합니다.
     *
     * @return 원본 Queue 빈
     */
    @Bean
    public Queue emailQueue() {
        return new Queue(emailQueue, true);
    }

    /**
     * 이메일 발송용 원본 Direct Exchange를 생성합니다.
     *
     * @return {@link DirectExchange} 빈
     */
    @Bean
    public DirectExchange emailExchange() {
        return new DirectExchange("email-exchange");
    }

    /**
     * 원본 큐와 원본 Direct Exchange를 바인딩합니다.
     * <p>
     * 라우팅 키는 "email-routing-key"로 지정됩니다.
     * </p>
     *
     * @param emailQueue    원본 큐 빈
     * @param emailExchange 원본 Direct Exchange 빈
     * @return {@link Binding} 빈
     */
    @Bean
    public Binding binding(Queue emailQueue, DirectExchange emailExchange) {
        return BindingBuilder.bind(emailQueue)
                .to(emailExchange)
                .with("email-routing-key");
    }
}
