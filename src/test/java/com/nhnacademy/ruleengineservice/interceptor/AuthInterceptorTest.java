package com.nhnacademy.ruleengineservice.interceptor;

import com.nhnacademy.ruleengineservice.auth.MemberThreadLocal;
import com.nhnacademy.ruleengineservice.dto.member.MemberDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthInterceptorTest {

    private AuthInterceptor authInterceptor;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        authInterceptor = new AuthInterceptor();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @AfterEach
    void tearDown() throws Exception {
        SecurityContextHolder.clearContext();
        MemberThreadLocal.removedMemberEmail();
        closeable.close();
    }

    @Test
    @DisplayName("Interceptor 성공")
    void preHandle() throws Exception {
        MemberDetails memberDetails = mock();
        when(memberDetails.getUsername()).thenReturn("test@test.com");

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                memberDetails, null, memberDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        boolean result = authInterceptor.preHandle(request, response, new Object());

        assertTrue(result);
        assertEquals("test@test.com", MemberThreadLocal.getMemberEmail());
    }

    @Test
    @DisplayName("interceptor 인증 실패")
    void preHandle_notAuthenticated_returnsFalse() throws Exception {
        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS");

        Authentication authentication = new AnonymousAuthenticationToken(
                "key", "anonymousUser", authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        boolean result = authInterceptor.preHandle(request, response, new Object());

        assertFalse(result);
    }

    @Test
    @DisplayName("삭제 됐는지 확인")
    void afterCompletion_clearsThreadLocal() throws Exception {
        MemberThreadLocal.setMemberEmail("test@test.com");
        authInterceptor.afterCompletion(request, response, new Object(), null);
        assertNull(MemberThreadLocal.getMemberEmail());
    }
}