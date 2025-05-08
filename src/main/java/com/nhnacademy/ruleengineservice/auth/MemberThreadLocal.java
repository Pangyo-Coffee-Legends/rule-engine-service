package com.nhnacademy.ruleengineservice.auth;

/**
 * MemberThreadLocal는 현재 요청 스레드 내에서 회원 이메일 정보를 안전하게 저장하고 조회할 수 있도록
 * ThreadLocal을 활용한 유틸리티 클래스입니다.
 *
 * <p>
 * 이 클래스는 인증 또는 인가 과정에서 추출한 회원 이메일을
 * 서비스/도메인 계층 등 어디서든 파라미터 전달 없이 손쉽게 사용할 수 있도록 도와줍니다.
 * </p>
 *
 * <ul>
 *   <li>{@link #setMemberEmail(String)} : 현재 스레드에 회원 이메일을 저장합니다.</li>
 *   <li>{@link #getMemberEmail()} : 현재 스레드에 저장된 회원 이메일을 반환합니다.</li>
 *   <li>{@link #removedMemberEmail()} : 현재 스레드에서 회원 이메일 정보를 제거합니다. (메모리 누수 방지)</li>
 * </ul>
 *
 * <b>주의:</b> 요청 처리 후 반드시 {@code removedMemberEmail()}을 호출하여 ThreadLocal을 정리해야 합니다.
 * (스레드풀 환경에서는 이전 요청의 정보가 남아있을 수 있습니다.)
 *
 * <p>
 * 이 클래스는 인스턴스 생성을 방지하기 위해 private 생성자를 갖습니다.
 * </p>
 *
 * @author 강승우
 */
public class MemberThreadLocal {
    private static final ThreadLocal<String> memberEmailLocal = new ThreadLocal<>();

    private MemberThreadLocal() { throw new IllegalStateException("Utility class"); }

    public static String getMemberEmail() { return memberEmailLocal.get(); }

    public static void setMemberEmail(String email) { memberEmailLocal.set(email); }

    public static void removedMemberEmail() { memberEmailLocal.remove(); }
}
