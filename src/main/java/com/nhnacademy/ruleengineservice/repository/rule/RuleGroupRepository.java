package com.nhnacademy.ruleengineservice.repository.rule;

import com.nhnacademy.ruleengineservice.domain.rule.RuleGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * RuleGroup 엔티티의 데이터 접근을 담당하는 레포지토리 인터페이스입니다.
 */
public interface RuleGroupRepository extends JpaRepository<RuleGroup, Long> {

    /**
     * 그룹 이름으로 RuleGroup을 조회합니다.
     *
     * @param ruleGroupName 그룹 이름
     * @return 이름이 일치하는 RuleGroup, 없으면 Optional.empty()
     */
    Optional<RuleGroup> findByRuleGroupName(String ruleGroupName);

    /**
     * 활성화된(Active) RuleGroup 목록을 조회합니다.
     *
     * @return 활성화된 RuleGroup 리스트
     */
    List<RuleGroup> findByActiveTrue();
}
