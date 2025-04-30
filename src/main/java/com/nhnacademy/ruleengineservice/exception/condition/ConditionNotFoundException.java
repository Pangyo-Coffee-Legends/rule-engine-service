package com.nhnacademy.ruleengineservice.exception.condition;

/**
 * 조건(Condition) 엔티티를 찾을 수 없을 때 발생하는 예외입니다.
 * <p>
 * 주로 conditions 테이블에서 conditionNo(PK) 또는 기타 식별자로
 * 조건을 조회할 때 존재하지 않을 경우 throw 됩니다.
 * </p>
 * <p>
 * 예시 사용 위치:
 * <ul>
 *   <li>ConditionService, RuleEngineService 등에서 조건 단건 조회 시</li>
 *   <li>조건 수정, 삭제, 평가 등에서 존재하지 않는 조건 번호로 접근할 때</li>
 * </ul>
 * </p>
 *
 * @author 강승우
 */
public class ConditionNotFoundException extends RuntimeException {
    public ConditionNotFoundException(Long message) {
        super("Condition Not Found : %d".formatted(message));
    }

    public ConditionNotFoundException(String message) {
        super("Condition Not Found : %s".formatted(message));
    }
}
