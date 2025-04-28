package com.nhnacademy.ruleengineservice.domain.trigger;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@DataJpaTest
@ActiveProfiles("test")
class TriggerEventTest {

    @Autowired
    EntityManager entityManager;

    @Test
    @DisplayName("TriggerEvent 생성 및 조회 검증")
    void createTriggerEvent_success() {
        String eventType = "INSERT";
        String eventParams = "{\"cron\":\"0 0 8 * * ?\",\"timeZone\":\"Asia/Seoul\"}";;

        TriggerEvent triggerEvent = TriggerEvent.ofNewTriggerEvent(eventType, eventParams);
        entityManager.persist(triggerEvent);
        entityManager.flush();
        entityManager.clear();

        TriggerEvent target = entityManager.find(TriggerEvent.class, triggerEvent.getEventNo());
        log.debug("TriggerEvent 저장 및 조회 성공: {}", target);

        assertNotNull(target);
        assertAll(
                () -> assertEquals(eventType, target.getEventType()),
                () -> assertEquals(eventParams, target.getEventParams())
        );
    }
}