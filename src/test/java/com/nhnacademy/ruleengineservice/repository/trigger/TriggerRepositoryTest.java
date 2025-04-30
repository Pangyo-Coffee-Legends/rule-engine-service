package com.nhnacademy.ruleengineservice.repository.trigger;

import com.nhnacademy.ruleengineservice.domain.rule.Rule;
import com.nhnacademy.ruleengineservice.domain.rule.RuleGroup;
import com.nhnacademy.ruleengineservice.domain.trigger.TriggerEvent;
import com.nhnacademy.ruleengineservice.repository.rule.RuleGroupRepository;
import com.nhnacademy.ruleengineservice.repository.rule.RuleRepository;
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
class TriggerRepositoryTest {

    @Autowired
    private RuleRepository ruleRepository;

    @Autowired
    private RuleGroupRepository ruleGroupRepository;

    @Autowired
    private TriggerRepository triggerRepository;

    private RuleGroup group;

    private Rule rule;

    @BeforeEach
    void setUp() {
        group = RuleGroup.ofNewRuleGroup("Test Group", "Description", 1);
        ruleGroupRepository.save(group);

        rule = Rule.ofNewRule(group, "Test Rule", "test description", 1);
        ruleRepository.save(rule);
    }

    @Test
    @DisplayName("Rule 로 TriggerEvent 조회")
    void findByRule() {
        TriggerEvent event = TriggerEvent.ofNewTriggerEvent(rule, "DB_INSERT", "{\"table\":\"users\"}");
        triggerRepository.save(event);

        List<TriggerEvent> found = triggerRepository.findByRule(rule);
        log.debug("findByRule 조회 : {}", found);

        assertNotNull(found);
        assertEquals("DB_INSERT", found.getFirst().getEventType());
    }

    @Test
    @DisplayName("이벤트 유형으로 트리거 이벤트 조회")
    void findByEventType() {
        TriggerEvent event1 = TriggerEvent.ofNewTriggerEvent(rule, "API_CALL", "{\"key\":\"value1\"}");
        TriggerEvent event2 = TriggerEvent.ofNewTriggerEvent(rule, "API_CALL", "{\"key\":\"value2\"}");
        TriggerEvent event3 = TriggerEvent.ofNewTriggerEvent(rule, "DB_INSERT", "{\"key\":\"value3\"}");

        triggerRepository.save(event1);
        triggerRepository.save(event2);
        triggerRepository.save(event3);

        List<TriggerEvent> found = triggerRepository.findByEventType("API_CALL");
        log.debug("findByEventType 조회 : {}", found);

        assertNotNull(found);
        assertEquals(2, found.size());
        assertTrue(found.stream().allMatch(e -> e.getEventType().equals("API_CALL")));
    }

    @Test
    @DisplayName("event params 에 특정 문자열이 포함된 트리거 이벤트 조회")
    void findByEventParams() {
        TriggerEvent event1 = TriggerEvent.ofNewTriggerEvent(rule, "API_CALL", "{\"key\":\"alarm\"}");
        TriggerEvent event2 = TriggerEvent.ofNewTriggerEvent(rule, "API_CALL", "{\"key\":\"notify\"}");
        TriggerEvent event3 = TriggerEvent.ofNewTriggerEvent(rule, "DB_INSERT", "{\"key\":\"alarm\"}");

        triggerRepository.save(event1);
        triggerRepository.save(event2);
        triggerRepository.save(event3);

        List<TriggerEvent> found = triggerRepository.findByEventParams("alarm");
        log.debug("findByEventParams 조회 : {}", found);

        assertNotNull(found);
        assertEquals(2, found.size());
        assertTrue(found.stream().allMatch(e -> e.getEventParams().contains("alarm")));
    }
}