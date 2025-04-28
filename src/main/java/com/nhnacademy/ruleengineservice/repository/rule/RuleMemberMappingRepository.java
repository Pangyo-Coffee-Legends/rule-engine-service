package com.nhnacademy.ruleengineservice.repository.rule;

import com.nhnacademy.ruleengineservice.domain.rule.RuleMemberMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * RuleMemberMapping 엔티티의 데이터 접근을 담당하는 레포지토리 인터페이스입니다.
 */
public interface RuleMemberMappingRepository extends JpaRepository<RuleMemberMapping, Long> {
    List<RuleMemberMapping> findByMbNo(Long mbNo);
}
