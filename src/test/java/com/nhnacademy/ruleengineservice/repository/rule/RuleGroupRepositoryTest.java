package com.nhnacademy.ruleengineservice.repository.rule;

import com.nhnacademy.ruleengineservice.domain.rule.RuleGroup;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@DataJpaTest
@ActiveProfiles("test")
class RuleGroupRepositoryTest {

    @Autowired
    RuleGroupRepository ruleGroupRepository;

    /**
     * 그룹 이름에 해당하는 RuleGroup 조회
     */
    @Test
    @DisplayName("그룹 이름으로 RuleGroup 조회")
    void findByRuleGroupName() {
        RuleGroup group = RuleGroup.ofNewRuleGroup("Test Group", "테스트 그룹", 1);
        ruleGroupRepository.save(group);

        Optional<RuleGroup> found = ruleGroupRepository.findByRuleGroupName("Test Group");
        log.debug("findByRuleGroupName 조회 : {}", found);

        assertTrue(found.isPresent());
        assertEquals("Test Group", found.get().getRuleGroupName());
    }

    /**
     * 활성화된 RuleGroup 만 조회
     */
    @Test
    @DisplayName("활성화된 RuleGroup 만 조회")
    void findByActiveTrue() {
        RuleGroup activeGroup = RuleGroup.ofNewRuleGroup("Active Group", "활성 그룹", 1);
        RuleGroup deactivateGroup = RuleGroup.ofNewRuleGroup("Deactivate Group", "비활성화 그룹", 1);

        deactivateGroup.setActive(false);

        ruleGroupRepository.save(activeGroup);
        ruleGroupRepository.save(deactivateGroup);

        List<RuleGroup> found = ruleGroupRepository.findByActiveTrue();
        log.debug("findByActiveTrue 조회 : {}", found);

        assertNotNull(found);
        assertTrue(found.stream().allMatch(g -> g.isActive() == true));
    }

    @Test
    @DisplayName("existsByRuleGroupName - 존재하는 그룹명")
    void existsByRuleGroupName() {
        RuleGroup group = RuleGroup.ofNewRuleGroup("TEST_GROUP", "description", 1);
        group.setActive(true);
        ruleGroupRepository.save(group);

        boolean exists = ruleGroupRepository.existsByRuleGroupName("TEST_GROUP");

        assertTrue(exists);
    }

    @Test
    @DisplayName("existsByRuleGroupName - 존재하지 않는 그룹명")
    void existsByRuleGroupName_whenNotExists_returnsFalse() {
        boolean exists = ruleGroupRepository.existsByRuleGroupName("NOT_EXIST_GROUP");

        assertFalse(exists);
    }
}