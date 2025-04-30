package com.nhnacademy.ruleengineservice.domain.rule;

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
class RuleMemberMappingTest {

    @Autowired
    EntityManager entityManager;

    @Test
    @DisplayName("RuleMemberMapping 객체 생성 및 필드 값 검증")
    void createRuleMemberMapping_success() {
        RuleGroup ruleGroup = RuleGroup.ofNewRuleGroup("그룹 테스트", "그룹 테스트 설명", 1);
        entityManager.persist(ruleGroup);

        Rule rule = Rule.ofNewRule(ruleGroup, "rule test", "rule test description", 3);
        entityManager.persist(rule);

        Long mbNo = 10L;

        RuleMemberMapping ruleMemberMapping = RuleMemberMapping.ofNewRuleMemberMapping(rule, mbNo);
        entityManager.persist(ruleMemberMapping);
        entityManager.flush();
        entityManager.clear();

        RuleMemberMapping target = entityManager.find(RuleMemberMapping.class, ruleMemberMapping.getMappingNo());
        log.debug("RuleMemberMapping 저장 및 조회 성공 : {}", target);

        assertNotNull(target);
        assertAll(
                () -> assertEquals(rule.getRuleNo(), target.getRule().getRuleNo()),
                () -> assertEquals(mbNo, target.getMbNo())
        );
    }
}