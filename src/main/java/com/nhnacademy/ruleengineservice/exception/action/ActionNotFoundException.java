package com.nhnacademy.ruleengineservice.exception.action;

/**
 * 액션(Action) 엔티티를 찾을 수 없을 때 발생하는 예외입니다.
 * <p>
 * 주로 actions 테이블에서 act_no(PK) 또는 기타 식별자로
 * 액션을 조회할 때 존재하지 않을 경우 throw 됩니다.
 * </p>
 * <p>
 * 예시 사용 위치:
 * <ul>
 *   <li>ActionService, RuleEngineService 등에서 액션 단건 조회 시</li>
 *   <li>액션 수정, 삭제, 실행 등에서 존재하지 않는 액션 번호로 접근할 때</li>
 * </ul>
 * </p>
 *
 * @author 강승우
 */
public class ActionNotFoundException extends RuntimeException {
    public ActionNotFoundException(Long message) {
        super("Action Not Found : %d".formatted(message));
    }

    public ActionNotFoundException(String message) { super(message); }
}
