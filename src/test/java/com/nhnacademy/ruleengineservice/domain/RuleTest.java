package com.nhnacademy.ruleengineservice.domain;

import com.nhnacademy.ruleengineservice.domain.rule.Rule;
import com.nhnacademy.ruleengineservice.domain.rule.RuleGroup;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <b>RuleTest</b>는 Rule 엔티티의 JPA 기반 CRUD(생성, 조회, 수정, 삭제) 동작을 검증하는 단위 테스트 클래스입니다.
 *
 * <p>
 * 주요 테스트 항목:
 * <ul>
 *   <li><b>findRule:</b> Rule 엔티티가 정상적으로 저장되고, 저장된 값과 생성일(createdAt)이 올바른지 검증합니다.</li>
 *   <li><b>updatedRule:</b> Rule 엔티티의 필드 값을 수정한 뒤, 수정 내용과 수정일(updatedAt)이 정상적으로 반영되는지 확인합니다.</li>
 *   <li><b>deleteRule:</b> Rule 엔티티를 삭제한 후, 실제로 데이터베이스에서 삭제되었는지 검증합니다.</li>
 * </ul>
 * </p>
 *
 * <p>
 * 테스트 환경:
 * <ul>
 *   <li>{@code @DataJpaTest}를 통해 인메모리 DB(H2 등)와 JPA 환경에서 테스트가 수행됩니다.</li>
 *   <li>{@code @ActiveProfiles("test")}로 테스트 전용 설정이 적용됩니다.</li>
 *   <li>{@code @Slf4j}를 통해 테스트 중 디버깅 로그를 출력할 수 있습니다.</li>
 *   <li>각 테스트는 독립적으로 실행되며, 테스트 후 데이터베이스는 롤백됩니다.</li>
 * </ul>
 * </p>
 *
 * @author 강승우
 */
@Slf4j
@ActiveProfiles("test")
@DataJpaTest
class RuleTest {

    @Autowired
    EntityManager entityManager;

    private RuleGroup ruleGroup;

    private Rule rule;

    /**
     * 각 테스트 실행 전 Rule 엔티티를 저장합니다.
     */
    @BeforeEach
    void setUp() {
        ruleGroup = RuleGroup.ofNewRuleGroup("그룹", "그룹 설명", 1);
        entityManager.persist(ruleGroup);

        rule = Rule.ofNewRule(ruleGroup, "테스트", "테스트 설명", 1);

        rule.setRuleGroup(ruleGroup);

        entityManager.persist(rule);
        entityManager.clear();
    }

    /**
     * Rule 엔티티가 정상적으로 저장되고, 저장된 값과 생성일(createdAt)이 올바른지 검증합니다.
     */
    @Test
    @DisplayName("저장 확인")
    void createdRule() {
        Rule dbRule = entityManager.find(Rule.class, rule.getRuleNo());
        log.debug("저장 확인 : {}", dbRule);

        Assertions.assertNotNull(dbRule);
        Assertions.assertAll(
                () -> assertEquals("테스트", rule.getRuleName()),
                () -> assertEquals("테스트 설명", rule.getRuleDescription()),
                () -> assertEquals(1, rule.getRulePriority()),
                () -> assertNotNull(rule.getCreatedAt()),
                () -> assertNull(rule.getUpdatedAt())
        );
    }

    @Test
    @DisplayName("active - false")
    void setRule() {
        Rule dbRule = entityManager.find(Rule.class, rule.getRuleNo());
        log.debug("active true: {}", dbRule);

        dbRule.setActive(false);

        entityManager.flush();
        entityManager.clear();

        Rule setRule = entityManager.find(Rule.class, rule.getRuleNo());
        log.debug("active false : {}", setRule);

        Assertions.assertNotNull(setRule);
        assertFalse(setRule.isActive());
    }

    /**
     * Rule 엔티티의 필드 값을 수정한 뒤, 수정 내용과 수정일(updatedAt)이 정상적으로 반영되는지 확인합니다.
     */
    @Test
    @DisplayName("수정 확인")
    void updatedRule() {
        Rule dbRule = entityManager.find(Rule.class, rule.getRuleNo());
        log.debug("수정 전 : {}", dbRule);

        dbRule.ruleUpdate("수정", "수정 테스트", 2);

        entityManager.flush();
        entityManager.clear();

        Rule updatedRule = entityManager.find(Rule.class, rule.getRuleNo());
        log.debug("수정 후 : {}", updatedRule);

        Assertions.assertNotNull(dbRule);
        Assertions.assertAll(
                () -> assertEquals("수정", updatedRule.getRuleName()),
                () -> assertEquals("수정 테스트", updatedRule.getRuleDescription()),
                () -> assertEquals(2, updatedRule.getRulePriority()),
                () -> assertNotNull(updatedRule.getCreatedAt()),
                () -> assertNotNull(updatedRule.getUpdatedAt())
        );
    }

    /**
     * Rule 엔티티를 삭제한 후, 실제로 데이터베이스에서 삭제되었는지 검증합니다.
     */
    @Test
    @DisplayName("삭제 확인")
    void deleteRule() {
        Rule dbRule = entityManager.find(Rule.class, rule.getRuleNo());
        log.debug("삭제 전 : {}", dbRule);

        entityManager.remove(dbRule);
        entityManager.flush();

        Rule deleteRule = entityManager.find(Rule.class, rule.getRuleNo());
        log.debug("삭제 후 : {}", deleteRule);

        Assertions.assertNull(deleteRule);
    }
}