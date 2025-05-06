package com.nhnacademy.ruleengineservice.exception.member;

/**
 * 인증 또는 권한이 없는 사용자가 접근을 시도할 때 발생하는 예외입니다.
 * <p>
 * 주로 인증이 필요한 리소스에 대해 인증 정보가 없거나,
 * 적절한 권한이 없는 사용자가 접근할 경우 throw됩니다.
 * </p>
 *
 * <pre>
 * 예시:
 * if (!user.hasPermission(resource)) {
 *     throw new UnauthorizedException("권한이 없습니다.");
 * }
 * </pre>
 *
 * @author 강승우
 */
public class UnauthorizedException extends RuntimeException {
    /**
     * 상세 메시지를 포함하는 UnauthorizedException을 생성합니다.
     *
     * @param message 예외의 상세 메시지
     */
    public UnauthorizedException(String message) {
        super(message);
    }
}
