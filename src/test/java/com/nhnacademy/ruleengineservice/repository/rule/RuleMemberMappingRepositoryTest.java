package com.nhnacademy.ruleengineservice.repository.rule;

import com.nhnacademy.ruleengineservice.domain.rule.Rule;
import com.nhnacademy.ruleengineservice.domain.rule.RuleGroup;
import com.nhnacademy.ruleengineservice.domain.rule.RuleMemberMapping;
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
class RuleMemberMappingRepositoryTest {

    @Autowired
    private RuleMemberMappingRepository ruleMemberMappingRepository;

    @Autowired
    private RuleRepository ruleRepository;

    @Autowired
    private RuleGroupRepository ruleGroupRepository;

    private RuleGroup group;

    private Rule rule;

    private RuleMemberMapping mapping;

    @BeforeEach
    void setUp() {
        group = RuleGroup.ofNewRuleGroup("Test Group", "테스트 그룹", 1);
        ruleGroupRepository.save(group);

        rule = Rule.ofNewRule(group, "test rule", "테스트 룰", 1);
        ruleRepository.save(rule);

        mapping = RuleMemberMapping.ofNewRuleMemberMapping(rule, 100L);
        ruleMemberMappingRepository.save(mapping);
    }

    @Test
    @DisplayName("RuleMemberMapping 저장 및 조회")
    void saveAndFindTest() {
        List<RuleMemberMapping> found = ruleMemberMappingRepository.findAll();
        log.debug("saveAndFindTest 조회 : {}", found);

        assertNotNull(found);
        assertEquals(100L, found.getFirst().getMbNo());
        assertEquals("test rule", found.getFirst().getRule().getRuleName());
    }

    @Test
    @DisplayName("특정 회원으로 RuleMemberMapping 조회")
    void findByMbNoTest() {
        Rule another = Rule.ofNewRule(group, "another", "another description", 2);
        ruleRepository.save(another);

        RuleMemberMapping mapping1 = RuleMemberMapping.ofNewRuleMemberMapping(rule, 200L);
        RuleMemberMapping mapping2 = RuleMemberMapping.ofNewRuleMemberMapping(another, 200L);

        ruleMemberMappingRepository.save(mapping1);
        ruleMemberMappingRepository.save(mapping2);

        List<RuleMemberMapping> found = ruleMemberMappingRepository.findByMbNo(200L);
        log.debug("findByMbNoTest 조회 : {}", found);

        assertNotNull(found);
        assertEquals(2, found.size());
        assertEquals(200L, found.getFirst().getMbNo());
    }
}