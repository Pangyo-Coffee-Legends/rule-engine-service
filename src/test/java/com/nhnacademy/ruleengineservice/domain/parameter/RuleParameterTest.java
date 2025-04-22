package com.nhnacademy.ruleengineservice.domain.parameter;

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
class RuleParameterTest {

    @Autowired
    EntityManager entityManager;

    @Test
    @DisplayName("rule parameter 검증")
    void createRuleParameter() {
        RuleParameter parameter = RuleParameter.ofNewRuleParameter("max_limit", "1000000");

        entityManager.persist(parameter);
        entityManager.flush();
        entityManager.clear();

        RuleParameter found = entityManager.find(RuleParameter.class, parameter.getParamNo());
        log.info("RuleParameter 저장 및 조회 성공: {}", found);

        Assertions.assertNotNull(found);
        Assertions.assertAll(
                () -> assertEquals("max_limit", found.getParamName()),
                () -> assertEquals("1000000", found.getParamValue())
        );
    }
}