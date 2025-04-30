package com.nhnacademy.ruleengineservice.exception.parameter;

/**
 * 파라미터(Parameter) 엔티티를 찾을 수 없을 때 발생하는 예외입니다.
 * <p>
 * 주로 rule_parameters 테이블에서 param_no(PK) 또는 기타 식별자로
 * 파라미터를 조회할 때 존재하지 않을 경우 throw 됩니다.
 * </p>
 * <p>
 * 예시 사용 위치:
 * <ul>
 *   <li>ParameterService에서 파라미터 단건 조회 시</li>
 *   <li>파라미터 수정, 삭제, 바인딩 등에서 존재하지 않는 파라미터 번호로 접근할 때</li>
 * </ul>
 * </p>
 *
 * @author 강승우
 */
public class ParameterNotFoundException extends RuntimeException {
    public ParameterNotFoundException(Long message) {
        super("Parameter Not Found : %d".formatted(message));
    }
}
