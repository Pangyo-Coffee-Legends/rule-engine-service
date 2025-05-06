package com.nhnacademy.ruleengineservice.exception.action;

/**
 * 액션 핸들러 실행 중 발생하는 예외를 나타냅니다.
 * <p>
 * 이 예외는 특정 액션 타입 처리 중 오류가 발생했을 때 throw 됩니다.
 * </p>
 */
public class ActionHandlerException extends RuntimeException {
    public ActionHandlerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ActionHandlerException(String message) {
        super(message);
    }
}
