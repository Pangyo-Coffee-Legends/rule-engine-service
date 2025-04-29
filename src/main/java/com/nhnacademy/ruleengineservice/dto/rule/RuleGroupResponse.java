package com.nhnacademy.ruleengineservice.dto.rule;

import jakarta.persistence.Column;

/**
 * 규칙 그룹(RuleGroup)의 정보를 클라이언트에 전달하기 위한 응답 DTO 클래스입니다.
 * <p>
 * 이 클래스는 rule_groups 테이블의 한 행을 표현하며,
 * 규칙 그룹의 기본 정보(식별자, 이름, 설명, 활성화 여부, 우선순위)를 포함합니다.
 * 클라이언트가 규칙 그룹 목록 조회, 상세 조회 등에서 활용할 수 있습니다.
 * <p>
 * <b>연관 테이블/필드:</b>
 * <ul>
 *   <li>ruleGroupNo: rule_groups 테이블의 rule_group_no</li>
 *   <li>ruleGroupName: rule_groups 테이블의 rule_group_name</li>
 *   <li>ruleGroupDescription: rule_groups 테이블의 rule_group_description</li>
 *   <li>active: rule_groups 테이블의 active</li>
 *   <li>priority: rule_groups 테이블의 priority</li>
 * </ul>
 *
 * @author 강승우
 */
public class RuleGroupResponse {

    /**
     * 규칙 그룹의 고유 식별자입니다.
     * rule_groups 테이블의 rule_group_no 컬럼과 매핑됩니다.
     */
    @Column(unique = true)
    private Long ruleGroupNo;

    /**
     * 규칙 그룹의 이름입니다.
     * rule_groups 테이블의 rule_group_name 컬럼과 매핑됩니다.
     */
    private String ruleGroupName;

    /**
     * 규칙 그룹의 상세 설명입니다.
     * rule_groups 테이블의 rule_group_description 컬럼과 매핑됩니다.
     */
    private String ruleGroupDescription;

    /**
     * 규칙 그룹의 활성화 여부입니다.
     * true 면 활성, false 면 비활성 상태를 의미합니다.
     * rule_groups 테이블의 active 컬럼과 매핑됩니다.
     */
    private boolean active;

    /**
     * 규칙 그룹의 우선순위입니다.
     * 숫자가 낮을수록 높은 우선순위를 의미합니다.
     * rule_groups 테이블의 priority 컬럼과 매핑됩니다.
     */
    private Integer priority;

    /**
     * 모든 필드를 초기화하는 생성자입니다.
     *
     * @param ruleGroupNo          규칙 그룹 식별자
     * @param ruleGroupName        규칙 그룹 이름
     * @param ruleGroupDescription 규칙 그룹 설명
     * @param priority             우선순위
     * @param active               활성화 여부
     */
    public RuleGroupResponse(Long ruleGroupNo, String ruleGroupName, String ruleGroupDescription, Integer priority, boolean active) {
        this.ruleGroupNo = ruleGroupNo;
        this.ruleGroupName = ruleGroupName;
        this.ruleGroupDescription = ruleGroupDescription;
        this.priority = priority;
        this.active = active;
    }

    public Long getRuleGroupNo() {
        return ruleGroupNo;
    }

    public String getRuleGroupName() {
        return ruleGroupName;
    }

    public String getRuleGroupDescription() {
        return ruleGroupDescription;
    }

    public boolean isActive() {
        return active;
    }

    public Integer getPriority() {
        return priority;
    }

    @Override
    public String toString() {
        return "RuleGroupResponse{" +
                "ruleGroupNo=" + ruleGroupNo +
                ", ruleGroupName='" + ruleGroupName + '\'' +
                ", ruleGroupDescription='" + ruleGroupDescription + '\'' +
                ", active=" + active +
                ", priority=" + priority +
                '}';
    }
}
