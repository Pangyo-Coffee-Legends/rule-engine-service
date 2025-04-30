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
     * <p>
     * rule_groups 테이블에서 rule_group_name 컬럼이 주어진 값과 일치하는 레코드를 찾습니다.
     * 룰 엔진에서 이름으로 그룹을 검색할 때 사용됩니다.
     * </p>
     *
     * @param ruleGroupName 조회할 그룹 이름
     * @return 이름이 일치하는 RuleGroup을 포함한 Optional 객체, 없으면 Optional.empty()
     */
    Optional<RuleGroup> findByRuleGroupName(String ruleGroupName);

    /**
     * 활성화된(Active) RuleGroup 목록을 조회합니다.
     * <p>
     * rule_groups 테이블에서 active 컬럼이 true인 모든 레코드를 조회합니다.
     * 규칙 엔진에서 현재 사용 가능한 그룹만 필터링할 때 사용됩니다.
     * </p>
     *
     * @return 활성화된 RuleGroup 객체들의 리스트
     */
    List<RuleGroup> findByActiveTrue();

    /**
     * 주어진 이름의 RuleGroup이 존재하는지 확인합니다.
     * <p>
     * rule_groups 테이블에서 rule_group_name 컬럼이 주어진 값과 일치하는 레코드가
     * 하나 이상 존재하는지 확인합니다. 새 그룹 생성 시 중복 검사에 활용될 수 있습니다.
     * </p>
     *
     * @param ruleGroupName 존재 여부를 확인할 그룹 이름
     * @return 이름이 일치하는 RuleGroup이 존재하면 true, 아니면 false
     */
    boolean existsByRuleGroupName(String ruleGroupName);
}
