package com.nhnacademy.ruleengineservice.config;

import com.nhnacademy.ruleengineservice.interceptor.AuthInterceptor;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class WebMvcConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthInterceptor authInterceptor;

    @BeforeEach
    void setUp() throws Exception {
        Mockito.when(authInterceptor.preHandle(any(), any(), any())).thenReturn(true);
    }

    @Test
    @DisplayName("인터셉터 제외 경로는 인터셉터가 동작하지 않는다")
    void excludePathPatternsTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/comfort/test"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/rules/9999"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/rule-groups/9999"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/rule-engine/test"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/conditions/9999"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/actions/9999"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

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
    @DisplayName("CORS 설정 테스트")
    void corsSettingsTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.options("/api/v1/anypath")
                        .header("Origin", "http://localhost:3000")
                        .header("Access-Control-Request-Method", "POST"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().string("Access-Control-Allow-Origin", "http://localhost:3000"))
                .andExpect(MockMvcResultMatchers.header().string("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE"))
                .andExpect(MockMvcResultMatchers.header().string("Access-Control-Allow-Credentials", "true"));
    }

    @Test
    @DisplayName("Preflight 요청")
    void testCorsPreflightRequest() throws Exception {
        mockMvc.perform(options("/api/v1/rules")
                        .header("Origin", "https://aiot2.live")
                        .header("Access-Control-Request-Method", "GET")
                        .header("Access-Control-Request-Headers", "content-type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "https://aiot2.live"))
                .andExpect(header().string("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE"))
                .andExpect(header().string("Access-Control-Allow-Headers", "content-type"));
    }
}