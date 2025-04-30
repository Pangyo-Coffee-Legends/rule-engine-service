package com.nhnacademy.ruleengineservice.repository.condition;

import com.nhnacademy.ruleengineservice.domain.condition.Condition;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@DataJpaTest
@ActiveProfiles("test")
class ConditionRepositoryTest {

    @Autowired
    EntityManager entityManager;

    @Autowired
    private ConditionRepository conditionRepository;

    @Autowired
    private RuleRepository ruleRepository;

    private Rule rule;

    @BeforeEach
    void setUp() {
        RuleGroup group = RuleGroup.ofNewRuleGroup("Test Group", "Test Group Description", 1);
        entityManager.persist(group);

        rule = Rule.ofNewRule(group, "Test Rule", "Test Description", 1);
        ruleRepository.save(rule);
    }

    @Test
    @DisplayName("Rule 로 Condition 조회")
    void findByRule() {
        Condition condition = Condition.ofNewCondition(rule,"EQ", "FieldA", "ValueA", 1);
        conditionRepository.save(condition);

        List<Condition> found = conditionRepository.findByRule(rule);
        log.debug("findByRule 조회 : {}", found);

        assertNotNull(found);
        assertEquals("EQ", found.getFirst().getConType());
    }

    @Test
    @DisplayName("ConType 으로 Condition 조회")
    void findByConType() {
        Condition condition = Condition.ofNewCondition(rule,"ConTypeB", "FieldB", "ValueB", 1);
        Condition condition1 = Condition.ofNewCondition(rule,"ConTypeB", "FieldC", "ValueC", 2);

        conditionRepository.save(condition);
        conditionRepository.save(condition1);

        List<Condition> found = conditionRepository.findByConType("ConTypeB");
        log.debug("findByConType 조회 : {}", found);

        assertNotNull(found);
        assertEquals("ConTypeB", found.getFirst().getConType());
    }

    @Test
    @DisplayName("conField 로 Condition 조회")
    void findByConField() {
        Condition condition = Condition.ofNewCondition(rule,"ConTypeC", "FiledC", "ValueC", 3);
        conditionRepository.save(condition);

        List<Condition> found = conditionRepository.findByConField("FiledC");
        log.debug("findByConField 조회 : {}", found);

        assertNotNull(found);
        assertEquals("FiledC", found.getFirst().getConField());
    }
}