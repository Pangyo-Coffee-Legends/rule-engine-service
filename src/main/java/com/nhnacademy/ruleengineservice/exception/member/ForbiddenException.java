package com.nhnacademy.ruleengineservice.exception.member;

/**
 * 사용자가 허용되지 않은 리소스 또는 기능에 접근하려고 시도할 때 throw되는 예외입니다.
 * <p>
 * 이 예외는 주로 인증은 되었지만 해당 요청에 대한 권한(Authorization)이 없는 경우에 사용됩니다.
 * 기본 메시지는 "접근이 거부되었습니다."입니다.
 * </p>
 *
 * <pre>
 * 예시:
 * if (!user.hasRole("ADMIN")) {
 *     throw new ForbiddenException();
 * }
 * </pre>
 *
 * @author 강승우
 */
public class ForbiddenException extends RuntimeException {
    /**
     * 기본 메시지("접근이 거부되었습니다.")를 포함하는 ForbiddenException을 생성합니다.
     */
    public ForbiddenException() {
        super("접근이 거부되었습니다.");
    }
}
