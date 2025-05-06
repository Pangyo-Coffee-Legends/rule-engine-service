package com.nhnacademy.ruleengineservice.exception.member;

/**
 * 지정된 멤버(사용자)를 찾을 수 없을 때 throw되는 예외입니다.
 * <p>
 * 이 예외는 주로 데이터베이스 또는 시스템 내에서 특정 멤버를 조회할 때
 * 해당 멤버가 존재하지 않는 경우에 사용됩니다.
 * </p>
 *
 * <pre>
 * 예시:
 * Member member = memberRepository.findById(id)
 *     .orElseThrow(() -> new MemberNotFoundException("해당 ID의 멤버를 찾을 수 없습니다: " + id));
 * </pre>
 *
 * @author 강승우
 */
public class MemberNotFoundException extends RuntimeException {
    /**
     * 상세 메시지를 포함하는 MemberNotFoundException을 생성합니다.
     *
     * @param message 예외의 상세 메시지
     */
    public MemberNotFoundException(String message) {
        super(message);
    }
}
