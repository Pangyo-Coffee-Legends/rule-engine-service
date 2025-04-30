package com.nhnacademy.ruleengineservice.domain.schedule;

import com.nhnacademy.ruleengineservice.domain.rule.Rule;
import com.nhnacademy.ruleengineservice.domain.rule.RuleGroup;
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
class RuleScheduleTest {

    @Autowired
    EntityManager entityManager;

    @Test
    @DisplayName("RuleSchedule 객체 생성 및 필드 값 검증")
    void createRuleSchedule_success() {
        RuleGroup group = RuleGroup.ofNewRuleGroup("group", "des", 1);
        entityManager.persist(group);

        Rule rule = Rule.ofNewRule(group, "rule", "description", 2);
        entityManager.persist(rule);

        String cronExpression = "0 0 12 * * ? *";
        String timeZone = "Asia/Seoul";

        RuleSchedule origin = RuleSchedule.ofNewRuleSchedule(rule, cronExpression, timeZone,5);
        entityManager.persist(origin);
        entityManager.flush();
        entityManager.clear();

        RuleSchedule ruleSchedule = entityManager.find(RuleSchedule.class, origin.getScheduleNo());
        log.debug("ruleSchedule 저장 및 조회 성공: {}", ruleSchedule);

        assertNotNull(ruleSchedule);
        assertAll(
                () -> assertEquals("0 0 12 * * ? *", ruleSchedule.getCronExpression()),
                () -> assertEquals("Asia/Seoul", ruleSchedule.getTimeZone()),
                () -> assertEquals(5, ruleSchedule.getMaxRetires()),
                () -> assertTrue(ruleSchedule.getActive())
        );
    }
}