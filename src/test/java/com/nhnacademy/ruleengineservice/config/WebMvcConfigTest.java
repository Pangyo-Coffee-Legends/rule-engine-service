package com.nhnacademy.ruleengineservice.config;

import com.nhnacademy.ruleengineservice.interceptor.AuthInterceptor;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class WebMvcConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthInterceptor authInterceptor;

    @Test
    @DisplayName("인터셉터가 적용되는 경로는 인터셉터가 동작한다")
    void interceptorAppliedPathTest() throws Exception {
        Mockito.doAnswer(invocation -> {
            HttpServletResponse response = invocation.getArgument(1);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "로그인 해주세요");
            return false;
        }).when(authInterceptor).preHandle(any(), any(), any());

        String requestBody = """
                {
                    "ruleGroup": 1,
                    "ruleName": "test",
                    "ruleDescription": "test description",
                    "rulePriority": 1
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/rules")
                        .header("X-USER", "USER")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isForbidden());

        Mockito.verify(authInterceptor).preHandle(any(), any(), any());
    }

    @Test
    @DisplayName("excludePathPatterns에 포함된 경로는 인터셉터가 동작하지 않는다")
    void interceptorExcludedPath() throws Exception {
        // AuthInterceptor.preHandle이 true를 반환하도록 설정
        when(authInterceptor.preHandle(any(), any(), any())).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/comfort/test"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        // excludePathPatterns에 포함된 경로는 preHandle이 호출되지 않음
        Mockito.verify(authInterceptor, Mockito.never()).preHandle(any(), any(), any());
    }
}