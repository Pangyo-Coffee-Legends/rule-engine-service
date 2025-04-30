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
 * RuleGroup 엔티티의 JPA 기반 CRUD 동작과 Rule 리스트 관리 기능을 검증하는 통합 테스트 클래스입니다.
 *
 * <p>이 테스트 클래스는 다음과 같은 주요 시나리오를 검증합니다:
 * <ul>
 *   <li>RuleGroup 엔티티의 기본 CRUD 동작</li>
 *   <li>RuleGroup에 Rule 추가/삭제 시 영속성 컨텍스트 관리</li>
 *   <li>Lazy Loading 컬렉션의 올바른 초기화 및 변경 감지</li>
 * </ul>
 *
 * <p>테스트 환경:
 * <ul>
 *   <li>@DataJpaTest를 사용한 JPA 레이어 테스트</li>
 *   <li>H2 인메모리 데이터베이스 사용</li>
 *   <li>영속성 컨텍스트 수동 관리(flush/clear)</li>
 * </ul>
 *
 * @author 강승우
 */
@Slf4j
@ActiveProfiles("test")
@DataJpaTest
class RuleGroupTest {

    @Autowired
    EntityManager entityManager;

    private RuleGroup ruleGroup;

    /**
     * 각 테스트 실행 전 초기 RuleGroup 엔티티를 생성하고 영속화합니다.
     */
    @BeforeEach
    void setUp() {
        ruleGroup = RuleGroup.ofNewRuleGroup(
                "test group",
                "test description",
                1
        );
        entityManager.persist(ruleGroup);
    }

    /**
     * RuleGroup 기본 저장 기능을 검증합니다.
     *
     * <p>테스트 시나리오:
     * <ol>
     *   <li>초기 설정에서 저장된 RuleGroup 조회</li>
     *   <li>필드 값 일치 여부 검증</li>
     * </ol>
     */
    @Test
    @DisplayName("저장 확인")
    void createdRuleGroup() {
        RuleGroup dbGroup = entityManager.find(RuleGroup.class, ruleGroup.getRuleGroupNo());
        log.debug("저장 확인 : {}", dbGroup);

        Assertions.assertNotNull(dbGroup);
        Assertions.assertAll(
                () -> assertEquals("test group", dbGroup.getRuleGroupName()),
                () -> assertEquals("test description", dbGroup.getRuleGroupDescription()),
                () -> assertEquals(1, dbGroup.getPriority())
        );
    }

    /**
     * RuleGroup에 다수의 Rule을 추가하는 기능을 검증합니다.
     *
     * <p>테스트 시나리오:
     * <ol>
     *   <li>5개의 Rule 생성 및 추가</li>
     *   <li>flush/clear를 통한 영속성 컨텍스트 리셋</li>
     *   <li>DB에서 재조회 후 리스트 크기 검증</li>
     * </ol>
     */
    @Test
    @DisplayName("List 추가 확인")
    void addList() {
        for (int i = 0; i < 5; i++) {
            Rule rule = Rule.ofNewRule(
                    ruleGroup,
                    "name" + i,
                    "description" +i,
                    i+2
            );

            ruleGroup.addRule(rule);
        }
        entityManager.flush();
        entityManager.clear();

        RuleGroup dbGroup = entityManager.find(RuleGroup.class, ruleGroup.getRuleGroupNo());
        log.debug("List 추가 확인 : {}", dbGroup);

        Assertions.assertNotNull(dbGroup);
        assertEquals(5, dbGroup.getRuleList().size());
    }

    /**
     * RuleGroup에서 특정 Rule을 제거하는 기능을 검증합니다.
     *
     * <p>테스트 시나리오:
     * <ol>
     *   <li>3개의 Rule 추가 및 영속화</li>
     *   <li>인덱스 기반 Rule 제거</li>
     *   <li>리스트 크기 변화 검증</li>
     * </ol>
     *
     * @see RuleGroup#removeRule(Rule)
     */
    @Test
    @DisplayName("List 제거 확인")
    void removeList() {
        Rule rule1 = Rule.ofNewRule(ruleGroup,"name1","description1",3);
        Rule rule2 = Rule.ofNewRule(ruleGroup,"name2","description2",4);
        Rule rule3 = Rule.ofNewRule(ruleGroup,"name3","description3",5);

        ruleGroup.addRule(rule1);
        ruleGroup.addRule(rule2);
        ruleGroup.addRule(rule3);

        entityManager.flush();
        entityManager.clear();

        RuleGroup dbGroup = entityManager.find(RuleGroup.class, ruleGroup.getRuleGroupNo());
        log.debug("List 제거 전 : {}", dbGroup);

        Rule removedRule1 = dbGroup.getRuleList().get(1); // 2번째 Rule 선택
        dbGroup.removeRule(removedRule1); // 리스트에서 제거

        entityManager.flush();
        entityManager.clear();

        RuleGroup removedGroup = entityManager.find(RuleGroup.class, ruleGroup.getRuleGroupNo());
        log.debug("List 제거 후 : {}", removedGroup);

        Assertions.assertNotNull(removedGroup);
        assertEquals(2, removedGroup.getRuleList().size());
    }

    /**
     * RuleGroup의 전체 Rule 리스트 초기화 기능을 검증합니다.
     *
     * <p>주의: Lazy Loading 컬렉션은 size() 호출로 강제 초기화 필요
     *
     * <p>테스트 시나리오:
     * <ol>
     *   <li>컬렉션 초기화 전 size() 호출로 Lazy Loading 강제 수행</li>
     *   <li>clearRules() 호출 후 영속성 컨텍스트 동기화</li>
     *   <li>DB에서 재조회 후 빈 리스트 확인</li>
     * </ol>
     */
    @Test
    @DisplayName("List 한 번에 비우기")
    void clearRules() {
        Rule rule1 = Rule.ofNewRule(ruleGroup,"name1","description1" ,3);
        Rule rule2 = Rule.ofNewRule(ruleGroup,"name2","description2" ,4);
        Rule rule3 = Rule.ofNewRule(ruleGroup,"name3","description3" ,5);

        ruleGroup.addRule(rule1);
        ruleGroup.addRule(rule2);
        ruleGroup.addRule(rule3);

        entityManager.flush();
        entityManager.clear();

        RuleGroup dbGroup = entityManager.find(RuleGroup.class, ruleGroup.getRuleGroupNo());
        log.debug("List 완전 제거 전 : {}", dbGroup);

        // Lazy 를 할 경우 호출 시 컬렉션이 초기화 되지 않아 변경 사항이 추적되지 않습니다.
        log.debug("List 현재 개수: {}", dbGroup.getRuleList().size());

        dbGroup.clearRules();

        entityManager.flush();
        entityManager.clear();

        RuleGroup removedGroup = entityManager.find(RuleGroup.class, ruleGroup.getRuleGroupNo());
        log.debug("List 완전 제거 후 : {}", removedGroup);

        Assertions.assertNotNull(removedGroup);
        assertTrue(removedGroup.getRuleList().isEmpty());
    }
}