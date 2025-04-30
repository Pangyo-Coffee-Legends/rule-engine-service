package com.nhnacademy.ruleengineservice.repository.parameter;

import com.nhnacademy.ruleengineservice.domain.parameter.RuleParameter;
import com.nhnacademy.ruleengineservice.domain.rule.Rule;
import com.nhnacademy.ruleengineservice.domain.rule.RuleGroup;
import com.nhnacademy.ruleengineservice.repository.rule.RuleRepository;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@DataJpaTest
@ActiveProfiles("test")
class RuleParameterRepositoryTest {

    @Autowired
    RuleParameterRepository ruleParameterRepository;

    @Autowired
    RuleRepository ruleRepository;

    @Autowired
    EntityManager entityManager;

    private RuleGroup group;

    private Rule rule;

    @BeforeEach
    void setUp() {
        group = RuleGroup.ofNewRuleGroup("test group", "test description", 1);
        entityManager.persist(group);

        rule = Rule.ofNewRule(group, "rule test", "decription test", 1);
        ruleRepository.save(rule);
    }

    @Test
    @DisplayName("rule 을 통해 조회 - parameter")
    void findByRule() {
        RuleParameter ruleParameter = RuleParameter.ofNewRuleParameter(rule, "param Name", "10000");
        ruleParameterRepository.save(ruleParameter);

        List<RuleParameter> found = ruleParameterRepository.findByRule(rule);
        log.debug("findByRule 조회 parameter : {}", found);

        assertNotNull(found);
        assertEquals(rule.getRuleNo(), found.getFirst().getRule().getRuleNo());
    }
}