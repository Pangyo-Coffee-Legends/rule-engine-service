package com.nhnacademy.ruleengineservice.domain.action;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@ActiveProfiles("test")
@DataJpaTest
class ActionTest {

    @Autowired
    EntityManager entityManager;

    @Test
    @DisplayName("Action 객체 생성 및 필드 값 검증")
    void createAction_success() {
        String actType = "EMAIL";
        String actParams = "{\"to\":\"test@company.com\",\"subject\":\"테스트\"}";
        Integer actPriority = 1;

        Action origin = Action.ofNewAction(actType, actParams, actPriority);
        entityManager.persist(origin);
        entityManager.flush();
        entityManager.clear();

        Action action = entityManager.find(Action.class, origin.getActNo());
        log.debug("action: {}", action);

        assertNotNull(action);
        Assertions.assertAll(
                () -> assertEquals(actType, action.getActType()),
                () -> assertEquals(actParams, action.getActParams()),
                () -> assertEquals(actPriority, action.getActPriority())
        );
    }
}