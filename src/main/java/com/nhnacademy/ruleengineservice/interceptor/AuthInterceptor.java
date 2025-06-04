package com.nhnacademy.ruleengineservice.interceptor;

import com.nhnacademy.ruleengineservice.auth.MemberThreadLocal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Objects;

/**
 * Spring MVC 인터셉터로, 인증된 사용자의 멤버 정보를 ThreadLocal에 저장 및 관리합니다.
 * <p>
 * 이 인터셉터는 Spring Security의 인증 컨텍스트를 활용하여
 * 요청 처리 전후로 사용자 정보를 관리하는 역할을 수행합니다.
 * </p>
 *
 * <p>주요 기능:
 * <ul>
 *   <li><b>preHandle</b>: 인증된 사용자의 멤버 번호를 ThreadLocal에 저장</li>
 *   <li><b>afterCompletion</b>: 요청 완료 시 ThreadLocal 데이터 정리 (메모리 누수 방지)</li>
 * </ul>
 * </p>
 *
 * <p>동작 흐름:
 * <ol>
 *   <li>요청 진입 시 인증 정보(SecurityContext) 확인</li>
 *   <li>인증된 사용자인 경우 MemberDetails에서 멤버 번호 추출</li>
 *   <li>멤버 번호를 {@link MemberThreadLocal}에 저장 (전역 접근 가능)</li>
 *   <li>컨트롤러/서비스 계층에서 {@link MemberThreadLocal#getMemberEmail()}로 조회 가능</li>
 *   <li>요청 처리 완료 후 ThreadLocal 데이터 삭제</li>
 * </ol>
 * </p>
 *
 * @author 강승우
 */
@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    /**
     * 요청 전처리: 인증된 사용자의 멤버 번호를 ThreadLocal에 저장합니다.
     *
     * @param request  현재 HTTP 요청 객체
     * @param response 현재 HTTP 응답 객체
     * @param handler  실행할 핸들러(컨트롤러 메서드)
     * @return 인증 실패 시 false 반환하여 요청 중단, 성공 시 true
     * @throws Exception 내부 예외 발생 시 상위 계층으로 전파
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String email = request.getHeader("X-USER");

        if (Objects.isNull(email) || email.isBlank()) {
            log.error("registerRule unauthorized");
            response.sendError(HttpStatus.FORBIDDEN.value(), "로그인 해주세요");
            return false;
        }

         MemberThreadLocal.setMemberEmail(email);

        return true;
    }

    /**
     * 요청 후처리: ThreadLocal에서 멤버 번호를 제거합니다.
     *
     * @param request  현재 HTTP 요청 객체
     * @param response 현재 HTTP 응답 객체
     * @param handler  실행된 핸들러
     * @param ex       요청 처리 중 발생한 예외 (있을 경우)
     * @throws Exception 내부 예외 발생 시 상위 계층으로 전파
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        MemberThreadLocal.removedMemberEmail();
        log.debug("memberThreadLocal remove success!");
    }
}
