package com.nhnacademy.ruleengineservice.repository.rule;

import com.nhnacademy.ruleengineservice.domain.rule.Rule;
import com.nhnacademy.ruleengineservice.domain.rule.RuleGroup;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RuleRepositoryTest는 Rule 엔티티의 JPA Repository 동작을 검증하는 단위 테스트 클래스입니다.
 *
 * <p>
 * 주요 테스트 시나리오:
 * <ul>
 *   <li><b>삽입 확인(createRule):</b> Rule 엔티티가 정상적으로 저장되고, 저장된 값이 올바른지 검증합니다.</li>
 *   <li><b>삭제 확인(deleteRule):</b> 저장된 Rule 엔티티를 삭제한 후, 실제로 DB에서 삭제되었는지 확인합니다.</li>
 *   <li><b>수정 확인(updateRule):</b> 저장된 Rule 엔티티의 값을 변경(수정)하고, 변경 사항이 DB에 반영되는지 검증합니다.</li>
 * </ul>
 *
 * <p>
 * 테스트 환경:
 * <ul>
 *   <li>{@code @DataJpaTest}를 통해 인메모리 DB(H2 등)와 JPA 환경에서 테스트가 수행됩니다.</li>
 *   <li>{@code @ActiveProfiles("test")}로 테스트 전용 설정이 적용됩니다.</li>
 *   <li>{@code @Slf4j}를 통해 테스트 중 디버깅 로그를 출력할 수 있습니다.</li>
 * </ul>
 *
 * <p>
 * 각 테스트는 독립적으로 실행되며, 테스트 후 데이터베이스는 롤백됩니다.
 *
 * @author 강승우
 */
@Slf4j
@ActiveProfiles("test")
@DataJpaTest
class RuleRepositoryTest {

    @Autowired
    EntityManager entityManager;

    @Autowired
    RuleRepository ruleRepository;

    private Rule rule;

    private RuleGroup group;

    /**
     * 각 테스트 실행 전 Rule 엔티티를 저장합니다.
     */
    @BeforeEach
    void setUp() {
        group = RuleGroup.ofNewRuleGroup("테스트", "테스트 그룹", 1);
        entityManager.persist(group);

        rule = ruleRepository.save(
                Rule.ofNewRule(group, "온도 저장", "온도를 저장하는 설명", 1)
        );
        ruleRepository.save(rule);
    }

    /**
     * Rule 엔티티가 정상적으로 저장되고, 저장된 값이 올바른지 검증합니다.
     */
    @Test
    @DisplayName("삽입 확인")
    void createRule() {
        Rule dbRole = ruleRepository.findById(rule.getRuleNo())
                .orElseThrow(() -> new NotFoundException("없음"));

        log.debug("저장 확인: {}", dbRole);

        Assertions.assertNotNull(rule);
        Assertions.assertAll(
                () -> assertEquals("온도 저장", rule.getRuleName()),
                () -> assertEquals("온도를 저장하는 설명", rule.getRuleDescription()),
                () -> assertEquals(1, rule.getRulePriority())
        );
    }

    /**
     * 저장된 Rule 엔티티를 삭제한 후, 실제로 DB에서 삭제되었는지 확인합니다.
     */
    @Test
    @DisplayName("삭제 확인")
    void deleteRule() {
        Long id = rule.getRuleNo();

        ruleRepository.deleteById(id);

        boolean exists = ruleRepository.findById(id).isPresent();
        Assertions.assertFalse(exists, "삭제 후에도 엔티티가 존재합니다!");
    }

    /**
     * 저장된 Rule 엔티티의 값을 변경(수정)하고, 변경 사항이 DB에 반영되는지 검증합니다.
     */
    @Test
    @DisplayName("수정 확인")
    void updateRule() {
        Rule dbRule = ruleRepository.findById(rule.getRuleNo())
                .orElseThrow(() -> new NotFoundException("없음"));

        log.debug("저장된 데이터 가져오기 : {}", dbRule);
        dbRule.ruleUpdate("새로운 정보", "새로운 정보 설명", 3);

        Rule updatedRule = ruleRepository.findById(rule.getRuleNo()).orElseThrow();
        log.debug("수정된 데이터 가져오기 : {}", updatedRule);

        Assertions.assertNotNull(updatedRule);
        Assertions.assertAll(
                () -> assertEquals("새로운 정보", updatedRule.getRuleName()),
                () -> assertEquals("새로운 정보 설명", updatedRule.getRuleDescription()),
                () -> assertEquals(3, updatedRule.getRulePriority())
        );
    }

    /**
     * 주어진 규칙 이름과 일치하는 모든 Rule 엔티티를 가져옵니다.
     */
    @Test
    @DisplayName("일치하는 리스트 조회")
    void findByRuleName() {
        RuleGroup anotherGroup = RuleGroup.ofNewRuleGroup("another group", "description", 1);
        entityManager.persist(anotherGroup);

        Rule rule1 = Rule.ofNewRule(anotherGroup, "온도 저장", "description", 1);
        ruleRepository.save(rule1);

        List<Rule> found = ruleRepository.findByRuleName("온도 저장");
        log.debug("findByRuleName 조회 : {}", found);

        assertNotNull(found);
        assertEquals("온도 저장", found.getFirst().getRuleName());
    }

    /**
     * Boolean 에 대하여 해당하는 모든 Rule 엔티티를 가져옵니다.
     */
    @Test
    @DisplayName("Active 에 해당하는 리스트 조회")
    void findByActive() {
        // setUp 에 의해 하나의 데이터가 들어가 있음
        Rule activeRule = Rule.ofNewRule(group, "Active Rule", "Description", 1);
        ruleRepository.save(activeRule);

        Rule deactiveRule = Rule.ofNewRule(group, "Deactivate Rule", "Description", 2);
        deactiveRule.setActive(false);
        ruleRepository.save(deactiveRule);

        List<Rule> activeRules = ruleRepository.findByActive(true);
        log.debug("activeRules 조회 : {}", activeRules);

        assertNotNull(activeRules);
        assertTrue(activeRules.getFirst().isActive());
        assertEquals(2, activeRules.size());
    }

    /**
     * Rule Group 에 해당하는 모든 Rule 엔티티를 가져옵니다.
     */
    @Test
    @DisplayName("Rule Group 에 해당하는 모든 리스트 조회")
    void findByRuleGroup() {
        RuleGroup anotherGroup = RuleGroup.ofNewRuleGroup("another", "description", 2);
        entityManager.persist(anotherGroup);

        Rule rule1 = Rule.ofNewRule(anotherGroup, "Group Rule", "Description", 1);
        Rule rule2 = Rule.ofNewRule(anotherGroup, "Another Rule", "Description", 2);

        ruleRepository.save(rule1);
        ruleRepository.save(rule2);

        List<Rule> groupRules = ruleRepository.findByRuleGroup(anotherGroup);
        log.debug("groupRules 조회: {}", groupRules);

        assertNotNull(groupRules);
        assertEquals(anotherGroup, groupRules.getFirst().getRuleGroup());
        assertEquals(2, groupRules.size());
    }

    /**
     * 주어진 규칙 번호와 일치하는 Rule 엔티티를 조회합니다.
     */
    @Test
    @DisplayName("번호에 해당하는 엔티티 조회")
    void findByRuleNo() {
        Optional<Rule> found = ruleRepository.findByRuleNo(rule.getRuleNo());
        log.debug("findByRuleNo 조회: {}", found);

        assertTrue(found.isPresent());
        assertEquals(rule.getRuleNo(), found.get().getRuleNo());
    }
}