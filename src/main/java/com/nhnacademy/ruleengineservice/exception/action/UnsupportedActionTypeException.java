package com.nhnacademy.ruleengineservice.exception.action;

/**
 * 지원되지 않는 액션 타입이 요청되었을 때 발생하는 런타임 예외입니다.
 * <p>
 * 이 예외는 시스템에서 정의되지 않은 액션 타입을 사용하려고 시도할 때 발생합니다.
 * </p>
 *
 * <p>예시 사용 사례:</p>
 * <pre>
 * if (!handler.supports(actType)) {
 *     throw new UnsupportedActionTypeException(actType);
 * }
 * </pre>
 */
public class UnsupportedActionTypeException extends RuntimeException {

    /**
     * 주어진 액션 타입으로 예외 객체를 생성합니다.
     *
     * @param actType 시스템에서 지원하지 않는 액션 타입 식별자
     *                (예: "UNDEFINED_ACTION")
     */
    public UnsupportedActionTypeException(String actType) {
        super("지원하지 않는 액션 타입: " + actType);
    }
}
