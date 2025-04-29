package com.nhnacademy.ruleengineservice.exception.rule;

/**
 * 규칙 그룹(RuleGroup) 엔티티를 찾을 수 없을 때 발생하는 예외입니다.
 * <p>
 * 주로 rule_groups 테이블에서 rule_group_no(PK) 또는 rule_group_name 으로
 * 규칙 그룹을 조회할 때 존재하지 않을 경우 throw 됩니다.
 * </p>
 * <p>
 * 예시 사용 위치:
 * <ul>
 *   <li>RuleGroupService에서 그룹 단건 조회 시</li>
 *   <li>규칙 생성 시 상위 그룹을 찾을 수 없는 경우</li>
 *   <li>그룹 수정, 삭제 등에서 존재하지 않는 그룹 번호로 접근할 때</li>
 * </ul>
 * </p>
 *
 * @author 강승우
 */
public class RuleGroupNotFoundException extends RuntimeException {
    public RuleGroupNotFoundException(Long ruleGroupNo) {
        super("Rule Group not found : %s".formatted(ruleGroupNo));
    }

    public RuleGroupNotFoundException(String message) {
        super("Rule Group not found : %s".formatted(message));
    }
}
