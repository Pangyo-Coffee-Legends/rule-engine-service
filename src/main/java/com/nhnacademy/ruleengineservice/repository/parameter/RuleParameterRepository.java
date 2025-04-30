package com.nhnacademy.ruleengineservice.repository.parameter;

import com.nhnacademy.ruleengineservice.domain.parameter.RuleParameter;
import com.nhnacademy.ruleengineservice.domain.rule.Rule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Rule Parameter 엔티티의 데이터 접근을 담당하는 레포지토리 인터페이스입니다.
 */
public interface RuleParameterRepository extends JpaRepository<RuleParameter, Long> {

    /**
     * 특정 Rule 과 관련된 모든 RuleParameter 을 조회합니다.
     *
     * @param rule 조회할 Rule 엔티티
     * @return 해당 Rule 에 속한 RuleParameter 리스트
     */
    List<RuleParameter> findByRule(Rule rule);
}
