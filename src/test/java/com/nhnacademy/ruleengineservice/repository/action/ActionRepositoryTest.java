package com.nhnacademy.ruleengineservice.repository.action;

import com.nhnacademy.ruleengineservice.domain.action.Action;
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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
class ActionRepositoryTest {

    @Autowired
    private ActionRepository actionRepository;

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

        rule = Rule.ofNewRule(group, "rule test", "description test", 1);
        ruleRepository.save(rule);
    }

    @Test
    @DisplayName("rule 을 통해 조회 - action")
    void findByRule() {
        Action action = Action.ofNewAction(rule, "HTTP", "{\"url\":\"https://example.com\"}", 1);
        actionRepository.save(action);

        List<Action> found = actionRepository.findByRule(rule);
        log.debug("findByRule 조회 action : {}", found);

        assertNotNull(found);
        assertEquals(rule.getRuleNo(), found.getFirst().getRule().getRuleNo());
    }

    @Test
    @DisplayName("act Type 을 통해 조회")
    void findByActType() {
        Action action = Action.ofNewAction(rule, "EMAIL", "{\"email\":\"test@nhnacademy\"}", 1);
        Action action1 = Action.ofNewAction(rule, "EMAIL", "{\"email\":\"test1@nhnacademy\"}", 2);
        Action action2 = Action.ofNewAction(rule, "EMAIL", "{\"email\":\"test2@nhnacademy\"}", 3);
        Action action3 = Action.ofNewAction(rule, "PUSH", "{\"push\":\"모바일 웹으로 경고가 도착했습니다\"}", 3);

        actionRepository.save(action);
        actionRepository.save(action1);
        actionRepository.save(action2);
        actionRepository.save(action3);

        List<Action> found = actionRepository.findByActType("EMAIL");
        log.debug("findByActType 조회 : {}", found);

        assertNotNull(found);
        assertEquals("EMAIL", found.getFirst().getActType());
    }

    @Test
    @DisplayName("ActParams 포함 문자열로 Action 조회")
    void findByActParamsContaining() {
        Action action = Action.ofNewAction(rule, "EMAIL", "{\"email\":\"test@nhnacademy\"}", 1);
        Action action1 = Action.ofNewAction(rule, "EMAIL", "{\"email\":\"test2@nhnacademy\"}", 2);
        Action action2 = Action.ofNewAction(rule, "EMAIL", "{\"email\":\"test3@nhnacademy\"}", 3);

        actionRepository.save(action);
        actionRepository.save(action1);
        actionRepository.save(action2);

        List<Action> found = actionRepository.findByActParamsContaining("email");
        log.debug("findByActParamsContaining 조회 : {}", found);

        assertNotNull(found);
        assertTrue(found.getFirst().getActParams().contains("test"));
    }
}