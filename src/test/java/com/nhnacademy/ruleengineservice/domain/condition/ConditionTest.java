package com.nhnacademy.ruleengineservice.domain.condition;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@DataJpaTest
@ActiveProfiles("test")
class ConditionTest {

    @Autowired
    EntityManager entityManager;

    @Test
    @DisplayName("Condition 객체 생성 및 필드 값 검증")
    void createCondition_success() {
        String conType = "GT";
        String conField = "score";
        String conValue = "80";
        Integer conPriority = 1;

        Condition cond = Condition.ofNewCondition(conType, conField, conValue, conPriority);
        entityManager.persist(cond);
        entityManager.flush();
        entityManager.clear();

        Condition condition = entityManager.find(Condition.class, cond.getConditionNo());
        log.debug("condition: {}", condition);

        Assertions.assertNotNull(condition);
        Assertions.assertAll(
                () -> assertEquals(conType, condition.getConType()),
                () -> assertEquals(conField, condition.getConField()),
                () -> assertEquals(conValue, condition.getConValue()),
                () -> assertEquals(conPriority, condition.getConPriority())
        );
    }
}