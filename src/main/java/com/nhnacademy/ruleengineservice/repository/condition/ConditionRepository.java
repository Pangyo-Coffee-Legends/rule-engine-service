package com.nhnacademy.ruleengineservice.repository.condition;

import com.nhnacademy.ruleengineservice.domain.condition.Condition;
import com.nhnacademy.ruleengineservice.domain.rule.Rule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Condition 엔티티의 데이터 접근을 담당하는 레포지토리 인터페이스입니다.
 */
public interface ConditionRepository extends JpaRepository<Condition, Long> {

    /**
     * 특정 Rule과 연관된 모든 Condition을 조회합니다.
     *
     * @param rule 조회할 Rule 엔티티
     * @return 해당 Rule에 속한 Condition 리스트
     */
    List<Condition> findByRule(Rule rule);

    /**
     * 조건 타입(conType)으로 Condition을 조회합니다.
     *
     * @param conType 조건 타입
     * @return 해당 타입의 Condition 리스트
     */
    List<Condition> findByConType(String conType);

    /**
     * 조건 필드(conField)로 Condition을 조회합니다.
     *
     * @param conField 조건 필드
     * @return 해당 필드의 Condition 리스트
     */
    List<Condition> findByConField(String conField);
}
