package com.nhnacademy.ruleengineservice.service.rule;

import com.nhnacademy.ruleengineservice.domain.rule.Rule;
import com.nhnacademy.ruleengineservice.dto.rule.RuleRegisterRequest;
import com.nhnacademy.ruleengineservice.dto.rule.RuleResponse;
import com.nhnacademy.ruleengineservice.dto.rule.RuleUpdateRequest;

import java.util.List;

/**
 * 비즈니스 규칙(Rule) 관리 및 실행을 담당하는 서비스 인터페이스입니다.
 * 규칙의 등록, 조회, 수정, 삭제, 실행 등 핵심 기능을 제공합니다.
 */
public interface RuleService {

    /**
     * 새로운 규칙을 등록합니다.
     *
     * @param request 규칙 등록 요청 DTO
     * @return 생성된 규칙의 DTO
     */
    RuleResponse registerRule(RuleRegisterRequest request);

    /**
     * 규칙 정보를 수정합니다.
     *
     * @param ruleNo 수정할 규칙의 식별자
     * @param request 수정 요청 DTO
     */
    RuleResponse updateRule(Long ruleNo, RuleUpdateRequest request);

    /**
     * 규칙을 삭제(비활성화)합니다.
     *
     * @param ruleNo 삭제할 규칙의 식별자
     */
    void deleteRule(Long ruleNo);

    /**
     * 규칙 정보를 조회 합니다.
     *
     * @param ruleNo 조회할 규칙의 식별자
     * @return       조회된 규칙의 DTO
     */
    RuleResponse getRule(Long ruleNo);

    /**
     * 모든 규칙을 조회 합니다.
     *
     * @return 규칙 목록 응답 DTO 리스트
     */
    List<RuleResponse> getAllRule();

    /**
     * 특정 그룹에 속한 규칙 목록을 조회 합니다.
     *
     * @param ruleGroupNo 그룹 식별자
     * @return 규칙 목록 응답 DTO 리스트
     */
    List<RuleResponse> getRulesByGroup(Long ruleGroupNo);

    /**
     * 규칙의 활성화/비활성화 상태를 변경합니다.
     *
     * @param ruleNo 규칙 식별자
     * @param active 활성화 여부
     */
    void setRuleActive(Long ruleNo, boolean active);

    /**
     * Rule Repository 에 있는 내용을 공통 처리하기 위한 메서드
     *
     * @param ruleNo rule 식별자
     * @return 규칙 엔티티
     */
    Rule getRuleEntity(Long ruleNo);
}
