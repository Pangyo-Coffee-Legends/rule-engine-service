package com.nhnacademy.ruleengineservice.dto.rule;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 규칙 그룹(RuleGroup) 등록 요청을 위한 DTO 클래스입니다.
 * <p>
 * 클라이언트가 새로운 규칙 그룹을 생성할 때 필요한 정보를 전달하는 데 사용됩니다.
 * 필수 입력값으로는 규칙 그룹명(ruleGroupName), 우선순위(priority)가 있으며,
 * 규칙 그룹 설명(ruleGroupDescription)은 선택적으로 입력할 수 있습니다.
 * <p>
 * <b>연관 테이블/필드:</b>
 * <ul>
 *   <li>ruleGroupName: rule_groups 테이블의 rule_group_name</li>
 *   <li>ruleGroupDescription: rule_groups 테이블의 rule_group_description</li>
 *   <li>active: rule_groups 테이블의 active</li>
 *   <li>priority: rule_groups 테이블의 priority</li>
 * </ul>
 *
 * @author [작성자]
 * @since 2025-04-27
 */
public class RuleGroupRegisterRequest {

    /**
     * 생성할 규칙 그룹의 이름입니다.
     * rule_groups 테이블의 rule_group_name 컬럼과 매핑됩니다.
     */
    @NotBlank(message = "규칙 그룹 이름은 필수 항목입니다.")
    @Column(unique = true)
    private final String ruleGroupName;

    /**
     * 생성할 규칙 그룹의 상세 설명입니다.
     * rule_groups 테이블의 rule_group_description 컬럼과 매핑됩니다.
     */
    @Size(max = 200, message = "규칙 그룹 상세는 최대 200자 입니다.")
    private final String ruleGroupDescription;

    /**
     * 규칙 그룹의 우선순위입니다.
     * 숫자가 낮을수록 높은 우선순위를 가집니다.
     * rule_groups 테이블의 priority 컬럼과 매핑됩니다.
     */
    @NotNull(message = "규칙 그룹 우선순위는 필수 항목입니다.")
    private final Integer priority;

    /**
     * 모든 필드를 초기화하는 생성자입니다.
     *
     * @param ruleGroupName        규칙 그룹명
     * @param ruleGroupDescription 규칙 그룹 설명
     * @param priority             우선순위
     */
    public RuleGroupRegisterRequest(String ruleGroupName, String ruleGroupDescription, Integer priority) {
        this.ruleGroupName = ruleGroupName;
        this.ruleGroupDescription = ruleGroupDescription;
        this.priority = priority;
    }

    public String getRuleGroupName() {
        return ruleGroupName;
    }

    public String getRuleGroupDescription() {
        return ruleGroupDescription;
    }

    public Integer getPriority() {
        return priority;
    }
}
