package com.nhnacademy.ruleengineservice.service.rule;

import com.nhnacademy.ruleengineservice.dto.rule.RuleGroupRegisterRequest;
import com.nhnacademy.ruleengineservice.dto.rule.RuleGroupResponse;

import java.util.List;

/**
 * 규칙 그룹(RuleGroup) 관리 기능을 제공하는 서비스 인터페이스입니다.
 * <p>
 * 규칙 그룹의 등록, 삭제, 단건/전체 조회, 활성화 등
 * rule_groups 테이블과 관련된 주요 비즈니스 로직을 정의합니다.
 * <p>
 * <b>주요 기능:</b>
 * <ul>
 *   <li>registerRuleGroup: 새로운 규칙 그룹 등록</li>
 *   <li>deleteRuleGroup: 규칙 그룹 삭제</li>
 *   <li>getRuleGroup: 규칙 그룹 단건 조회</li>
 *   <li>getAllRuleGroups: 전체 규칙 그룹 목록 조회</li>
 *   <li>activateRuleGroup: 규칙 그룹 활성화</li>
 * </ul>
 *
 * @author 강승우
 */
public interface RuleGroupService {

    /**
     * 새로운 규칙 그룹을 등록합니다.
     *
     * @param request 규칙 그룹 등록 요청 DTO
     * @return 생성된 규칙 그룹의 응답 DTO
     */
    RuleGroupResponse registerRuleGroup(RuleGroupRegisterRequest request);

    /**
     * 규칙 그룹을 삭제합니다.
     *
     * @param ruleGroupNo 삭제할 규칙 그룹의 식별자
     */
    void deleteRuleGroup(Long ruleGroupNo);

    /**
     * 규칙 그룹의 상세 정보를 조회합니다.
     *
     * @param ruleGroupNo 조회할 규칙 그룹의 식별자
     * @return 규칙 그룹 응답 DTO
     */
    RuleGroupResponse getRuleGroup(Long ruleGroupNo);

    /**
     * 전체 규칙 그룹 목록을 조회합니다.
     *
     * @return 규칙 그룹 응답 DTO 리스트
     */
    List<RuleGroupResponse> getAllRuleGroups();

    /**
     * 규칙 그룹을 활성화/비활성화 상태를 변경합니다.
     *
     * @param ruleGroupNo 규칙 그룹의 식별자
     * @param active      활성화 여부
     */
    void setRuleGroupActive(Long ruleGroupNo, boolean active);
}
