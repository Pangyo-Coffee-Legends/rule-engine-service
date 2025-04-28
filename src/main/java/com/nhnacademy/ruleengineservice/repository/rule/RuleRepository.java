package com.nhnacademy.ruleengineservice.repository.rule;

import com.nhnacademy.ruleengineservice.domain.rule.Rule;
import com.nhnacademy.ruleengineservice.domain.rule.RuleGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Rule 엔티티의 데이터 접근을 담당하는 레포지토리 인터페이스입니다.
 */
public interface RuleRepository extends JpaRepository<Rule, Long> {

    /**
     * 주어진 규칙 이름과 일치하는 모든 Rule 엔티티를 조회합니다.
     *
     * @param ruleName 조회할 규칙 이름
     * @return 규칙 이름이 일치하는 Rule 엔티티 리스트
     */
    List<Rule> findByRuleName(String ruleName);

    /**
     * 해당되는 상태의 모든 Rule 엔티티를 조회합니다.
     *
     * @return 해당하는 상태의 Rule 엔티티 리스트
     */
    List<Rule> findByActive(boolean active);

    /**
     * 특정 RuleGroup에 속한 모든 Rule 엔티티를 조회합니다.
     *
     * @param ruleGroup 조회할 RuleGroup 엔티티
     * @return 해당 그룹에 속한 Rule 엔티티 리스트
     */
    List<Rule> findByRuleGroup(RuleGroup ruleGroup);

    /**
     * 주어진 규칙 번호(ruleNo)와 일치하는 Rule 엔티티를 조회합니다.
     *
     * @param ruleNo 조회할 규칙 번호
     * @return 규칙 번호가 일치하는 Rule 엔티티, 없으면 Optional.empty()
     */
    Optional<Rule> findByRuleNo(Long ruleNo);
}
