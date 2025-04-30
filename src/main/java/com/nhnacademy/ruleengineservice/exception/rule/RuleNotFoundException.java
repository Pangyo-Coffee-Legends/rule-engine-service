package com.nhnacademy.ruleengineservice.exception.rule;

/**
 * 규칙(Rule) 엔티티를 찾을 수 없을 때 발생하는 예외입니다.
 * <p>
 * 주로 rules 테이블에서 rule_no(PK) 또는 기타 식별자로
 * 규칙을 조회할 때 존재하지 않을 경우 throw 됩니다.
 * </p>
 * <p>
 * 예시 사용 위치:
 * <ul>
 *   <li>RuleService, RuleEngineService 등에서 규칙 단건 조회 시</li>
 *   <li>규칙 수정, 삭제, 실행 등에서 존재하지 않는 규칙 번호로 접근할 때</li>
 *   <li>조건, 액션, 파라미터 등을 생성할 때 상위 규칙을 찾을 수 없는 경우</li>
 * </ul>
 * </p>
 *
 * @author 강승우
 */
public class RuleNotFoundException extends RuntimeException {
  public RuleNotFoundException(Long ruleNo) {
    super("Rule not found : %s".formatted(ruleNo));
  }
  public RuleNotFoundException(String message) {
    super("Rule not found : %s".formatted(message));
  }
}
