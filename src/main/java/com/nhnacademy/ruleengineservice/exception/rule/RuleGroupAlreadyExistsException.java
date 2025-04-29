package com.nhnacademy.ruleengineservice.exception.rule;

/**
 * 이미 존재하는 규칙 그룹(RuleGroup) 이름으로 생성을 시도할 때 발생하는 예외입니다.
 * <p>
 * rule_groups 테이블에서 rule_group_name 컬럼의 유일성 제약조건을 위반할 때 발생합니다.
 * 주로 새로운 규칙 그룹 생성 과정에서 중복 이름 검증에 사용됩니다.
 * </p>
 * <p>
 * 예시 사용 위치:
 * <ul>
 *   <li>RuleGroupService의 registerRuleGroup 메서드에서 중복 검사 시</li>
 *   <li>규칙 그룹 생성 API 엔드포인트에서 이름 검증 시</li>
 * </ul>
 * </p>
 *
 * @author 강승우
 */
public class RuleGroupAlreadyExistsException extends RuntimeException {
    public RuleGroupAlreadyExistsException(String ruleGroupName) {
        super("Already exists %s".formatted(ruleGroupName));
    }
}
