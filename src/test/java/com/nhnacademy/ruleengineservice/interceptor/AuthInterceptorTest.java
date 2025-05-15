package com.nhnacademy.ruleengineservice.interceptor;

import com.nhnacademy.ruleengineservice.auth.MemberThreadLocal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
class AuthInterceptorTest {

    private AuthInterceptor authInterceptor;

    private MockHttpServletRequest request;

    private MockHttpServletResponse response;

    private final Object handler = new Object();

    @BeforeEach
    void setUp() {
        authInterceptor = new AuthInterceptor();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        MemberThreadLocal.removedMemberEmail();
    }

    @AfterEach
    void tearDown() {
        MemberThreadLocal.removedMemberEmail();
    }

    @Test
    @DisplayName("Interceptor 성공")
    void preHandle() throws Exception {
        // 요청헤드에서 이메일 꺼내서 등록 MemberThreadLocal 에 등록
        String testEmail = "user@example.com";
        request.addHeader("X-USER", testEmail);

        boolean result = authInterceptor.preHandle(request, response, handler);

        assertTrue(result);
        assertEquals(testEmail, MemberThreadLocal.getMemberEmail());
        assertEquals(200, response.getStatus());
    }

    @Test
    @DisplayName("헤더가 없는 경우")
    void preHandle_notAuthenticated_returnsFalse() throws Exception {
        boolean result = authInterceptor.preHandle(request, response, handler);

        assertFalse(result);
        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
        assertTrue(Objects.isNull(MemberThreadLocal.getMemberEmail()));
    }

    @Test
    @DisplayName("빈 헤더인 경우")
    void preHandle_blankHeader_shouldReturnForbidden() throws Exception {
        request.addHeader("X-USER", "   ");

        boolean result = authInterceptor.preHandle(request, response, handler);

        assertFalse(result);
        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
        assertNull(MemberThreadLocal.getMemberEmail());
    }

    @Test
    @DisplayName("삭제 됐는지 확인")
    void afterCompletion_clearsThreadLocal() {
        String testEmail = "user@example.com";
        MemberThreadLocal.setMemberEmail(testEmail);

        authInterceptor.afterCompletion(request, response, handler, null);

        assertNull(MemberThreadLocal.getMemberEmail());
    }
}